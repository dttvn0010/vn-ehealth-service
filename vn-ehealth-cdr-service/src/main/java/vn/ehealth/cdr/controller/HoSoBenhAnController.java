package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.cdr.validate.JsonParser;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.io.*;

@RestController
@RequestMapping("/api/hsba")
public class HoSoBenhAnController {
    
    private static Logger logger = LoggerFactory.getLogger(HoSoBenhAnController.class);
    
    @Value("${server.upload.path}")
    private String uploadPath;
    
    private ObjectMapper objectMapper = EmrUtils.createObjectMapper();
    
    private JsonParser jsonParser = new JsonParser();
    
    private static Properties fieldsConvertProp = new Properties();
        
    static {
        try {
            fieldsConvertProp.load(new ClassPathResource("fields_convert.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Cannot read fieldsConvert properties", e);
        }
        
    }
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;    
    @Autowired private BenhNhanService benhNhanService;
    @Autowired private CoSoKhamBenhService coSoKhamBenhService;
    @Autowired private LogService logService;
    @Autowired private EncounterDao encounterDao; 
    
    @Autowired UserService userService;

    @GetMapping("/count_ds_hs")
    public ResponseEntity<?> countHsba(@RequestParam int trangthai, @RequestParam String mayte) {
        try {
            var user = UserUtil.getCurrentUser();
            var count = hoSoBenhAnService.countHoSo(user.get().id, user.get().coSoKhamBenhId, trangthai, mayte);
            return ResponseEntity.ok(count);
            
        }catch (Exception e) {
            return EmrUtils.errorResponse(e);
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
                                                @RequestParam String mayte,
                                                @RequestParam int start, 
                                                @RequestParam int count) {
        
        try {
            var user = UserUtil.getCurrentUser();    
            var result = hoSoBenhAnService.getDsHoSo(user.get().id, user.get().coSoKhamBenhId, trangthai, mayte, start, count);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
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
    
    private String preprocessJsonFields(String jsonSt) {
        for(var entry: fieldsConvertProp.entrySet()) {
            String field = (String) entry.getKey();
            String fieldReplace = (String) entry.getValue();
            jsonSt = jsonSt.replace("\"" + field + "\"", "\"" + fieldReplace + "\"");
        }
        return jsonSt;
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
        
        jsonSt = preprocessJsonFields(jsonSt);
        
        try {
            var map = jsonParser.parseJson(jsonSt);
            
            var benhNhan = (Map<String, Object>) map.get("emrBenhNhan");
            String idhis = (String) benhNhan.get("idhis");
            var emrBenhNhan = benhNhanService.getByIdhis(idhis);
            if(emrBenhNhan.isEmpty()) {
                throw new Exception(String.format("emrBenhNhan with idhis %s not found, please create this patient first", idhis));
            }
            
            var coSoKhamBenh = (Map<String, Object>) map.get("emrCoSoKhamBenh");            
            var emrCoSoKhamBenh = coSoKhamBenhService.getByMa((String) coSoKhamBenh.get("ma")).orElseThrow();
            
        	var hsba = objectMapper.convertValue(map, HoSoBenhAn.class);
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            hsba = hoSoBenhAnService.createOrUpdateFromHIS(userId, emrBenhNhan.get().id, emrCoSoKhamBenh.id, hsba, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba);
            
            var result = mapOf(
                "success" , true,
                "emrHoSoBenhAn", hsba  
            );
            
            return ResponseEntity.ok(result);
        
        } catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
}