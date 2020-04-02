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
import vn.ehealth.cdr.model.HinhAnhTonThuong;
import vn.ehealth.cdr.service.HinhAnhTonThuongService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/hatt")
public class HinhAnhTonThuongController {
    @Autowired 
    private HinhAnhTonThuongService hinhAnhTonThuongService;
    @Autowired HoSoBenhAnService hoSoBenhAnService;
    
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();

    @GetMapping("/get_hatt")
    public ResponseEntity<?> getHatt(@RequestParam("hatt_id") String id) {
        var hatt = hinhAnhTonThuongService.getById(new ObjectId(id));
        return ResponseEntity.of(hatt);
    }
    
    @GetMapping("/get_ds_hatt")
    public ResponseEntity<?> getDsHatt(@RequestParam("hsba_id") String hsbaId) {
        var hattList = hinhAnhTonThuongService.getByHoSoBenhAnId(new ObjectId(hsbaId));
        return ResponseEntity.ok(hattList);
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hatt")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElseThrow();
            
            var hattObjList = (List<Object>) map.get("dsHinhAnhTonThuong");
            var hattList = hattObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, HinhAnhTonThuong.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            hinhAnhTonThuongService.createOrUpdateFromHIS(userId, hsba, hattList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "hattList", hattList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}
