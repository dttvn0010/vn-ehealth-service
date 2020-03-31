package vn.ehealth.emr.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.model.EmrFileDinhKem;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.service.EmrBenhNhanService;
import vn.ehealth.emr.service.EmrCoSoKhamBenhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.UserService;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_HOSO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;
import vn.ehealth.emr.validate.JsonParser;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.io.*;

@RestController
@RequestMapping("/api/hsba")
public class EmrHoSoBenhAnController {
    
    private static Logger logger = LoggerFactory.getLogger(EmrHoSoBenhAnController.class);
    
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
    
    @Autowired private EmrHoSoBenhAnService emrHoSoBenhAnService;    
    @Autowired private EmrBenhNhanService emrBenhNhanService;
    @Autowired private EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    @Autowired private EncounterDao encounterDao; 
    
    @Autowired UserService userService;

    @GetMapping("/count_ds_hs")
    public ResponseEntity<?> countHsba(@RequestParam int trangthai, @RequestParam String mayte) {
        try {
            var user = UserUtil.getCurrentUser();
            var count = emrHoSoBenhAnService.countHoSo(user.get().id, user.get().emrCoSoKhamBenhId, trangthai, mayte);
            return ResponseEntity.ok(count);
            
        }catch (Exception e) {
            return EmrUtils.errorResponse(e);
        }        
    }
    
    @GetMapping("/get_ds_hs_by_bn")
    public ResponseEntity<?> getDsHsbaByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var result = emrHoSoBenhAnService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_ds_hs")
    public ResponseEntity<?> getDsHsba(@RequestParam int trangthai ,
                                                @RequestParam String mayte,
                                                @RequestParam int start, 
                                                @RequestParam int count) {
        
        try {
            var user = UserUtil.getCurrentUser();    
            var result = emrHoSoBenhAnService.getDsHoSo(user.get().id, user.get().emrCoSoKhamBenhId, trangthai, mayte, start, count);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }        
    }
    
    @GetMapping("/count_hsba_logs")
    public ResponseEntity<?> countHsbaLogs(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(emrHoSoBenhAnService.countHistory(new ObjectId(id)));
    } 
    
    @GetMapping("/get_hsba_logs")
    public ResponseEntity<?> getHsbaLogs(@RequestParam("hsba_id") String id, @RequestParam int start, @RequestParam int count) {        
        return ResponseEntity.ok(emrHoSoBenhAnService.getHistory(new ObjectId(id), start, count));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(mapOf("hsGoc", emrHoSoBenhAnService.getHsgoc(new ObjectId(id))));
    }
    
    
    @GetMapping("/get_hsba_by_id")
    public ResponseEntity<?> getHsbaById(@RequestParam("hsba_id") String id) {        
        var hsba = emrHoSoBenhAnService.getById(new ObjectId(id));
        return ResponseEntity.of(hsba);
    }
    
    @GetMapping("/archive_hsba")
    public ResponseEntity<?> archiveHsba(@RequestParam("hsba_id") String id) {
        
        try {
            var user = UserUtil.getCurrentUser();
            emrHoSoBenhAnService.archiveHsba(new ObjectId(id), user.get().id);
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
            emrHoSoBenhAnService.unArchiveHsba(new ObjectId(id), user.get().id);
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
            emrHoSoBenhAnService.deleteHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(mapOf("success", false, "error", error));
        }
    }
    

    @PostMapping("/update_hsba")
    public ResponseEntity<?> updateHsba(@RequestBody String jsonSt) {
        try {
            var hsba = objectMapper.readValue(jsonSt, EmrHoSoBenhAn.class);
            var user = UserUtil.getCurrentUser();
            hsba = emrHoSoBenhAnService.update(hsba, user.get().id);          
            
            var result = mapOf(
                "success" , true,
                "emrHoSoBenhAn", hsba 
            );
                        
            return ResponseEntity.ok(result);            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
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
    
    private void saveToFhirDb(EmrHoSoBenhAn hsba) {
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
            var emrBenhNhan = emrBenhNhanService.getByIdhis(idhis);
            if(emrBenhNhan.isEmpty()) {
                throw new Exception(String.format("emrBenhNhan with idhis %s not found, please create this patient first", idhis));
            }
            
            var coSoKhamBenh = (Map<String, Object>) map.get("emrCoSoKhamBenh");            
            var emrCoSoKhamBenh = emrCoSoKhamBenhService.getByMa((String) coSoKhamBenh.get("ma")).orElseThrow();
            
        	var hsba = objectMapper.convertValue(map, EmrHoSoBenhAn.class);
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            
            hsba = emrHoSoBenhAnService.createOrUpdateFromHIS(userId, emrBenhNhan.get().id, emrCoSoKhamBenh.id, hsba, jsonSt);
            
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
    
    @GetMapping("/report")
    public ResponseEntity<?> getReportHsba(String maLoaiBenhAn, String maCoSoKhamBenh, Optional<String> startDate, Optional<String> endDate) {
        var fromDate = startDate.map(x -> DateUtil.parseStringToDate(x, DateUtil.FORMAT_DD_MM_YYYY_HH_MM)).orElse(null);
        var toDate = endDate.map(x -> DateUtil.parseStringToDate(x, DateUtil.FORMAT_DD_MM_YYYY_HH_MM)).orElse(null);
        var emrCoSoKhamBenhId = emrCoSoKhamBenhService.getByMa(maCoSoKhamBenh).map(x -> x.id).orElse(null);
        var hsbaList = emrHoSoBenhAnService.getReport(emrCoSoKhamBenhId, maLoaiBenhAn, fromDate, toDate);
        var groups = new HashMap<>();
        
        for(var hsba : hsbaList) {
            if(!groups.containsKey(hsba.emrDmLoaiBenhAn.ma)) {
                groups.put(hsba.emrDmLoaiBenhAn.ma, new int[2]);
            }
            var group = (int[]) groups.get(hsba.emrDmLoaiBenhAn.ma);
            if(hsba.trangThai == TRANGTHAI_HOSO.CHUA_XULY) {
                group[0] += 1;
            }else if(hsba.trangThai == TRANGTHAI_HOSO.DA_LUU) {
                group[1] += 1;
            }
        }
        
        return ResponseEntity.ok(groups);
        
    }
    
    private String modifyFilename(String filename) {        
        long time = System.currentTimeMillis();
        int pos = filename.lastIndexOf('.');
        if(pos >= 0) {
            return filename.substring(0, pos) + "_" + String.valueOf(time) + filename.substring(pos);
        }else {
            return filename + "_" + String.valueOf(time);
        }        
    }
    
    @PostMapping("/add_giayto")
    public ResponseEntity<?> addGiayToDinhKem(@RequestParam("hsba_id") String id, @RequestParam("attached_files") MultipartFile[] attachedFiles) {
        try {
            var emrFileDinhKems = new ArrayList<EmrFileDinhKem>();
            
            for(var attachedFile : attachedFiles) {
                var filename = modifyFilename(attachedFile.getOriginalFilename());
                var file = new File(uploadPath + "/" + filename);
                attachedFile.transferTo(file);
                
                var emrFileDinhKem = new EmrFileDinhKem();
                emrFileDinhKem.url = "/upload/" + filename;
                emrFileDinhKem.ten = attachedFile.getOriginalFilename();
                emrFileDinhKems.add(emrFileDinhKem);
            }
            
            emrHoSoBenhAnService.addEmrFileDinhKems(new ObjectId(id), emrFileDinhKems);
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
    
    @PostMapping("/add_user_view_hsba")
    public ResponseEntity<?> addUserViewHSBA(@RequestParam("hsba_id") String id, @RequestParam("userId") String userId) {
    	try {
    	 emrHoSoBenhAnService.addUserViewHSBA(new ObjectId(id), new ObjectId(userId));
    	 return ResponseEntity.ok(mapOf("success", true));
    	 
    	}catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
       
    }
    
    @PostMapping("/delete_user_view_hsba")
    public ResponseEntity<?> deleteUserViewHSBA(@RequestParam("hsba_id") String id, @RequestParam("userId") String userId) {
    	 try {
    		 emrHoSoBenhAnService.deleteUserViewHSBA(new ObjectId(id), new ObjectId(userId));
             return ResponseEntity.ok(mapOf("success", true));
             
         }catch(Exception e) {
             return EmrUtils.errorResponse(e);
         }
    }
}