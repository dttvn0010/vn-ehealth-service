package vn.ehealth.cdr.controller;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cda.CDAUtils;
import vn.ehealth.cda.TemplateUtils;
import vn.ehealth.cdr.controller.helper.EncounterHelper;
import vn.ehealth.cdr.controller.helper.OrganizationHelper;
import vn.ehealth.cdr.controller.helper.PatientHelper;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.BenhNhanService;
import vn.ehealth.cdr.service.CoSoKhamBenhService;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.LogService;
import vn.ehealth.cdr.service.PhauThuatThuThuatService;
import vn.ehealth.cdr.service.XetNghiemService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.common.UnAuthorizedException;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/hsba")
public class HoSoBenhAnController {

    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;    
    @Autowired private BenhNhanService benhNhanService;
    @Autowired private CoSoKhamBenhService coSoKhamBenhService;
    @Autowired private PhauThuatThuThuatService phauThuatThuThuatService;
    @Autowired private XetNghiemService xetNghiemService;
    @Autowired private DonThuocService donThuocService;
    
    @Autowired private LogService logService;
    @Autowired private EncounterDao encounterDao; 
    @Autowired private PatientHelper patientHelper;
    @Autowired private OrganizationHelper organizationHelper;
    @Autowired private EncounterHelper encounterHelper;
    
    @GetMapping("/count_ds_hs")
    public ResponseEntity<?> countHsba(@RequestParam int trangthai, @RequestParam String maYte) {
        try {
            var user = UserUtil.getCurrentUser().orElse(null);
            
            if(user == null) {
                throw new UnAuthorizedException();
            }
            
            var count = hoSoBenhAnService.countHoSo(user.id, user.coSoKhamBenhId, trangthai, maYte);
            return ResponseEntity.ok(count);
            
        }catch (Exception e) {
            return ResponseUtil.errorResponse(e);
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
            var user = UserUtil.getCurrentUser().orElse(null);
            
            if(user == null) {
                throw new UnAuthorizedException();
            }
            
            var result = hoSoBenhAnService.getDsHoSo(user.id, user.coSoKhamBenhId, trangthai, maYte, start, count);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
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
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/unarchive_hsba")
    public ResponseEntity<?> unArchiveHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();            
            hoSoBenhAnService.unArchiveHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/export_cda/{id}")
    public ResponseEntity<?> exportCDA(@PathVariable("id") String id) {
        try {
            var hsbaId = new ObjectId(id);
            var hsba = hoSoBenhAnService.getById(hsbaId).orElseThrow();
            
            var dsPttt = phauThuatThuThuatService.getByHoSoBenhAnId(hsbaId);
            var dsXetNghiem = xetNghiemService.getByHoSoBenhAnId(hsbaId);
            var dsDonThuoc = donThuocService.getByHoSoBenhAnId(hsbaId);
            
            var data = mapOf(
                "utils", TemplateUtils.class,
                "hsba", JsonUtil.objectToMap(hsba),
                "benhNhan", JsonUtil.objectToMap(hsba.getBenhNhan()),
                "cskb", JsonUtil.objectToMap(hsba.getCoSoKhamBenh()),
                "dsPttt", transform(dsPttt, x -> JsonUtil.objectToMap(x)) ,
                "dsXetNghiem", transform(dsXetNghiem, x -> JsonUtil.objectToMap(x)),
                "dsDonThuoc", transform(dsDonThuoc, x -> JsonUtil.objectToMap(x))
            );
            
            var xml = CDAUtils.getClinicalDocument(JsonUtil.objectToMap(data));
            var xmlBytes = xml.getBytes();
            var resource = new ByteArrayResource(xmlBytes);
            
            return ResponseEntity.ok()
                    .contentLength(xmlBytes.length)
                    .contentType(MediaType.parseMediaType("application/xml"))
                    .header("Content-disposition", "attachment; filename=cda_" + id + ".xml")
                    .body(resource);
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/delete_hsba")
    public ResponseEntity<?> deleteHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();
            hoSoBenhAnService.deleteHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }

    private IdType getHsbaFhirId(@Nonnull HoSoBenhAn hsba) {
        var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
        
        if(enc != null) {
            return enc.getIdElement();
        }
        
        return null;
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba) throws Exception {
        if(hsba == null) return;
        
        var benhNhan = hsba.benhNhan;
        var cskb = hsba.coSoKhamBenh;
        
        if(benhNhan == null || cskb == null) return;
        
        var patient = patientHelper.getPatientBySobhyt(benhNhan.sobhyt);
        var serviceProvider = organizationHelper.getOrganizationByMa(cskb.ma);
        
        var encounter = hsba.toFhir(patient, serviceProvider);
        
        if(encounter != null) {
            var fhirId = getHsbaFhirId(hsba);
            
            if(fhirId == null) {
                encounterDao.create(encounter);
            }else {
                encounterDao.update(encounter, fhirId);
            }            
        }
    }
    
    @SuppressWarnings("unchecked")
    @PostMapping("/create_or_update_hsba")
    public ResponseEntity<?> createOrUpdateHsbaHIS(@RequestBody String jsonSt) {        
        
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            
            var benhNhanMap = (Map<String, Object>) map.get("benhNhan");
            String idhis = (String) benhNhanMap.get("idhis");
            var benhNhan = benhNhanService.getByIdhis(idhis).orElse(null);
            
            if(benhNhan == null) {
                throw new Exception(String.format("benhNhan with idhis %s not found, please create this patient first", idhis));
            }
            
            var coSoKhamBenhMap = (Map<String, Object>) map.get("coSoKhamBenh");
            String maCskb = (String) coSoKhamBenhMap.get("ma");
            var coSoKhamBenh = coSoKhamBenhService.getByMa(maCskb).orElse(null);
            
            if(coSoKhamBenh == null) {
                throw new Exception(String.format("coSoKhamBenh with ma=%s not found", maCskb));
            }
            
            var hsba = objectMapper.convertValue(map, HoSoBenhAn.class);
            hsba = hoSoBenhAnService.createOrUpdateFromHIS(benhNhan.id, coSoKhamBenh.id, hsba, jsonSt);
            
            // save to FHIR db
            saveToFhirDb(hsba);
            
            var result = mapOf(
                "success" , true,
                "hoSoBenhAn", hsba  
            );
            
            return ResponseEntity.ok(result);
        
        } catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}