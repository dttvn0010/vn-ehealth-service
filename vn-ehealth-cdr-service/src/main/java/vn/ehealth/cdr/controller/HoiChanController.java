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
import vn.ehealth.cdr.model.HoiChan;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.HoiChanService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/hoichan")
public class HoiChanController {
    @Autowired 
    private HoiChanService hoiChanService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @GetMapping("/get_ds_hoichan")
    public ResponseEntity<?> getDsHoichan(@RequestParam("hsba_id") String id) {
        var hoichanList = hoiChanService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(hoichanList);
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hoi_chan")
    public ResponseEntity<?> createOrUpdateHoiChanFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var hcObjList = (List<Object>) map.get("dsHoiChan");
            var hcList = hcObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, HoiChan.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            hoiChanService.createOrUpdateFromHIS(userId, hsba, hcList, hcObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "hcList", hcList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
    
}
