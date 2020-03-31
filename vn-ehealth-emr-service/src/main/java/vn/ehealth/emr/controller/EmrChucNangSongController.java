package vn.ehealth.emr.controller;

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

import vn.ehealth.emr.model.EmrChucNangSong;
import vn.ehealth.emr.service.EmrChucNangSongService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/chucnangsong")
public class EmrChucNangSongController {
	
	private JsonParser jsonParser = new JsonParser();
	private ObjectMapper objectMapper = EmrUtils.createObjectMapper();

	@Autowired private EmrChucNangSongService emrChucNangSongService;
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;
    
    @GetMapping("/get_ds_chucnangsong_by_bn")
    public ResponseEntity<?> getDsChucNangSongByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        return ResponseEntity.ok(emrChucNangSongService.getByEmrBenhNhanId(new ObjectId(benhNhanId)));
    }
    
    @GetMapping("/get_ds_chucnangsong")
    public ResponseEntity<?> getDsChucNangSong(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(emrChucNangSongService.getByEmrHoSoBenhAnId(new ObjectId(id)));
    }

    @GetMapping("/delete_chucnangsong")
    public ResponseEntity<?> deleteChucnangsong(@RequestParam("chucnangsong_id") String id) {
        try {
        	var user = UserUtil.getCurrentUser();
            emrChucNangSongService.delete(new ObjectId(id), user.get().id);
            var result = mapOf("success" , true);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/save_chucnangsong")
    public ResponseEntity<?> saveChucnangsong(@RequestBody String jsonSt) {
        
        try {
            var mapper = EmrUtils.createObjectMapper();
            var chucnangsong = mapper.readValue(jsonSt, EmrChucNangSong.class);
            var user = UserUtil.getCurrentUser();
            chucnangsong = emrChucNangSongService.save(chucnangsong, user.get().id, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "emrChucNangSong", chucnangsong 
            );
                    
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_chuc_nang_song")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = emrHoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var cnsObjList = (List<Object>) map.get("emrChucNangSongs");
            var cnsList = cnsObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, EmrChucNangSong.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);            
            
            emrChucNangSongService.createOrUpdateFromHIS(userId, hsba, cnsList, cnsObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "cnsList", cnsList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @GetMapping("/count_chucnangsong_logs")
    public ResponseEntity<?> countLogs(@RequestParam("chucnangsong_id") String id) {        
        return ResponseEntity.ok(emrChucNangSongService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_chucnangsong_logs")
    public ResponseEntity<?> getLogs(@RequestParam("chucnangsong_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrChucNangSongService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("chucnangsong_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrChucNangSongService.getHsgoc(new ObjectId(id))));
    }
}
