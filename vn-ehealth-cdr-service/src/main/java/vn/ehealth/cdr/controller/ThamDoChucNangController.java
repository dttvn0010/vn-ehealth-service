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
import vn.ehealth.cdr.model.ThamDoChucNang;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.ThamDoChucNangService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/tdcn")
public class ThamDoChucNangController {
    
    @Autowired 
    private ThamDoChucNangService thamDoChucNangService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @GetMapping("/get_ds_tdcn")
    public ResponseEntity<?> getDsThamDoChucNang(@RequestParam("hsba_id") String id) {
        var tdcnList = thamDoChucNangService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(tdcnList);
    }
        
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_tdcn")
    public ResponseEntity<?> createOrUpdateTdcnFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var tdcnObjList = (List<Object>) map.get("emrThamDoChucNangs");
            var tdcnList = tdcnObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, ThamDoChucNang.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            thamDoChucNangService.createOrUpdateFromHIS(userId, hsba, tdcnList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "tdcnList", tdcnList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
}
