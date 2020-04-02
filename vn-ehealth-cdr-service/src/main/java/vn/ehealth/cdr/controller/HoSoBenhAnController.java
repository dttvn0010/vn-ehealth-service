package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.auth.service.UserService;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.BenhNhanService;
import vn.ehealth.cdr.service.CoSoKhamBenhService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.validate.JsonParser;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/hsba")
public class HoSoBenhAnController {

    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    private JsonParser jsonParser = new JsonParser();
       
    @Autowired private HoSoBenhAnService hoSoBenhAnService;    
    @Autowired private BenhNhanService benhNhanService;
    @Autowired private CoSoKhamBenhService coSoKhamBenhService;
    @Autowired private LogService logService;
    @Autowired private EncounterDao encounterDao; 
    
    @Autowired UserService userService;

    @GetMapping("/count_ds_hs")
    public ResponseEntity<?> countHsba(@RequestParam int trangthai, @RequestParam String maYte) {
        try {
            var user = UserUtil.getCurrentUser();
            var count = hoSoBenhAnService.countHoSo(user.get().id, user.get().coSoKhamBenhId, trangthai, maYte);
            return ResponseEntity.ok(count);
            
        }catch (Exception e) {
            return CDRUtils.errorResponse(e);
        }        
    }
    
    @GetMapping("/get_all_ids")
    public List<String> getAllIds() {
        return hoSoBenhAnService.getAllIds();
    }
    
    @GetMapping("/get_full_log/{id}")
    public List<String> getFullLog(@PathVariable String id) {
        return logService.getHsbaFullLogs(new ObjectId(id));
    }
    
    @GetMapping("/get_ds_hs")
    public ResponseEntity<?> getDsHsba(@RequestParam int trangthai ,
                                                @RequestParam String maYte,
                                                @RequestParam int start, 
                                                @RequestParam int count) {
        
        try {
            var user = UserUtil.getCurrentUser();    
            var result = hoSoBenhAnService.getDsHoSo(user.get().id, user.get().coSoKhamBenhId, trangthai, maYte, start, count);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }        
    }
    
    @GetMapping("/count_hsba_logs")
    public ResponseEntity<?> countHsbaLogs(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(hoSoBenhAnService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_hsba_logs")
    public ResponseEntity<?> getHsbaLogs(@RequestParam("hsba_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(hoSoBenhAnService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", hoSoBenhAnService.getHsgoc(new ObjectId(id))));
    }
    
    
    @GetMapping("/get_hsba_by_id")
    public ResponseEntity<?> getHsbaById(@RequestParam("hsba_id") String id) {        
        var hsba = hoSoBenhAnService.getById(new ObjectId(id));
        return ResponseEntity.of(hsba);
    }
    
    @GetMapping("/archive_hsba")
    public ResponseEntity<?> archiveHsba(@RequestParam("hsba_id") String id) {
        
        try {
            var user = UserUtil.getCurrentUser();
            hoSoBenhAnService.archiveHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(mapOf("success", false, "error", error));
        }
    }
    
    @GetMapping("/unarchive_hsba")
    public ResponseEntity<?> unArchiveHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();            
            hoSoBenhAnService.unArchiveHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(mapOf("success", false, "error", error));
        }
    }
    
    @GetMapping("/delete_hsba")
    public ResponseEntity<?> deleteHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();
            hoSoBenhAnService.deleteHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(mapOf("success", false, "error", error));
        }
    }

    private List<Encounter> getVkEncounterList(Encounter hsbaEncounter) {
        if(hsbaEncounter != null) {
            var parent = (Object) (ResourceType.Encounter + "/" + hsbaEncounter.getId());
            var params = mapOf("partOf.reference", parent);    
            return encounterDao.searchResource(MongoUtils.createCriteria(params));
        }
        
        return new ArrayList<>();
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba) {
        if(hsba == null) return;
        
        try {
            var encounterDb = hsba.getEncounterInDB();
            if(encounterDb != null) {
                var oldVkEncounters = getVkEncounterList(encounterDb);
                oldVkEncounters.forEach(x -> encounterDao.remove(x.getIdElement()));
            }
            
            var encounters = hsba.toFhir();
            if(encounters.size() > 0) {
                var hsbaEncounter = encounters.get(0);
                if(encounterDb != null) {
                    hsbaEncounter = encounterDao.update(hsbaEncounter, encounterDb.getIdElement());
                }else {
                    hsbaEncounter = encounterDao.create(hsbaEncounter);
                }
                for(int i = 1; i < encounters.size(); i++) {
                    var vkEnc = encounters.get(i);
                    vkEnc.setPartOf(FhirUtil.createReference(hsbaEncounter));
                    encounterDao.create(vkEnc);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hsba")
    public ResponseEntity<?> createOrUpdateHsbaHIS(@RequestBody String jsonSt) {        
        
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = jsonParser.parseJson(jsonSt);
            
            var benhNhanMap = (Map<String, Object>) map.get("benhNhan");
            String idhis = (String) benhNhanMap.get("idhis");
            var benhNhan = benhNhanService.getByIdhis(idhis);
            if(benhNhan.isEmpty()) {
                throw new Exception(String.format("benhNhan with idhis %s not found, please create this patient first", idhis));
            }
            
            var coSoKhamBenhMap = (Map<String, Object>) map.get("coSoKhamBenh");            
            var coSoKhamBenh = coSoKhamBenhService.getByMa((String) coSoKhamBenhMap.get("ma")).orElseThrow();
            
        	var hsba = objectMapper.convertValue(map, HoSoBenhAn.class);
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            hsba = hoSoBenhAnService.createOrUpdateFromHIS(userId, benhNhan.get().id, coSoKhamBenh.id, hsba, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba);
            
            var result = mapOf(
                "success" , true,
                "hoSoBenhAn", hsba  
            );
            
            return ResponseEntity.ok(result);
        
        } catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }
}