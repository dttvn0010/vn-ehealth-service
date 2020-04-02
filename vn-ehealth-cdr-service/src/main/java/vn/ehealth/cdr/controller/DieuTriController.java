package vn.ehealth.cdr.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.service.DieuTriService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/dieutri")
public class DieuTriController {
    
    @Autowired 
    private DieuTriService dieuTriService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_dieutri")
    public ResponseEntity<?> getDsDieutri(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(dieuTriService.getByHoSoBenhAnId(new ObjectId(id)));
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_dieu_tri")
    public ResponseEntity<?> createOrUpdateDieuTriFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var dtObjList = (List<Object>) map.get("dsDieuTri");
            var dtList = dtObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, DieuTri.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            dieuTriService.createOrUpdateFromHIS(userId, hsba, dtList, dtObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "dtList", dtList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
