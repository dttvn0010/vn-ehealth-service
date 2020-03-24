package vn.ehealth.emr.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.model.EmrBenhNhan;
import vn.ehealth.emr.service.EmrBenhNhanService;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.validate.JsonParser;

@RestController
@RequestMapping("/api/benh_nhan")
public class EmrBenhNhanController {
    
    private static Logger logger = LoggerFactory.getLogger(EmrBenhNhanController.class);
    private JsonParser jsonParser = new JsonParser();
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    @Autowired EmrBenhNhanService emrBenhNhanService;
    
    @GetMapping("/count_benhnhan")
    public long countBenhNhan(@RequestParam String keyword) {
        return emrBenhNhanService.countBenhNhan(keyword);
    }
    
    @GetMapping("/search_benhnhan")
    public ResponseEntity<?> searchBenhNhan(@RequestParam String keyword, 
                                            @RequestParam Optional<Integer> start, 
                                            @RequestParam Optional<Integer> count) {
        var emrBenhNhans = emrBenhNhanService.searchBenhNhan(keyword, start.orElse(-1), count.orElse(-1));
        return ResponseEntity.ok(emrBenhNhans);
    }
    
    @GetMapping("/get_benhnhan_by_id")
    public ResponseEntity<?> getBenhNhan(@RequestParam String id) {
        var benhNhan = emrBenhNhanService.getById(new ObjectId(id));
        return ResponseEntity.of(benhNhan);
    }

    @PostMapping("/create_or_update_benhnhan")
    public ResponseEntity<?> createOrUpdateBenhNhan(@RequestBody String jsonSt) {
        try {
            var map = jsonParser.parseJson(jsonSt);
            var benhNhan = objectMapper.convertValue(map, EmrBenhNhan.class);
            if(StringUtils.isEmpty(benhNhan.iddinhdanhchinh)) {
                benhNhan.iddinhdanhchinh = benhNhan.idhis;
            }
            
            if(StringUtils.isEmpty(benhNhan.iddinhdanhchinh)) {
                throw new RuntimeException("Empty iddinhdanhchinh");
            }
            
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            benhNhan = emrBenhNhanService.createOrUpdate(userId, benhNhan, jsonSt);
            
            var result = Map.of(
                "success" , true,
                "emrBenhNhan", benhNhan 
            );
                    
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error create/update benhnhan:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }        
    }
}
