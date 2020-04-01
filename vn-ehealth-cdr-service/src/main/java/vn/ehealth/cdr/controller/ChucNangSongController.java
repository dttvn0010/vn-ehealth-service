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
import vn.ehealth.cdr.model.ChucNangSong;
import vn.ehealth.cdr.service.ChucNangSongService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/chucnangsong")
public class ChucNangSongController {
	
	private JsonParser jsonParser = new JsonParser();
	private ObjectMapper objectMapper = EmrUtils.createObjectMapper();

	@Autowired private ChucNangSongService chucNangSongService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    
    @GetMapping("/get_ds_chucnangsong")
    public ResponseEntity<?> getDsChucNangSong(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(chucNangSongService.getByHoSoBenhAnId(new ObjectId(id)));
    }

    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_chuc_nang_song")
    public ResponseEntity<?> createOrUpdateChamSocFromHIS(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var matraodoiHsba = (String) map.get("matraodoiHoSo");
            var hsba = hoSoBenhAnService.getByMatraodoi(matraodoiHsba).orElseThrow();
            
            var cnsObjList = (List<Object>) map.get("emrChucNangSongs");
            var cnsList = cnsObjList.stream()
                                .map(obj -> objectMapper.convertValue(obj, ChucNangSong.class))
                                .collect(Collectors.toList());
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);            
            
            chucNangSongService.createOrUpdateFromHIS(userId, hsba, cnsList, cnsObjList, jsonSt);
            
            var result = mapOf(
                "success" , true,
                "cnsList", cnsList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
}
