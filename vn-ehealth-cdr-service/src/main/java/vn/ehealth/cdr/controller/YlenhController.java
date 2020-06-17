package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.controller.helper.MedicationRequestHelper;
import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.dto.YlenhDVKTDTO;
import vn.ehealth.cdr.model.dto.YlenhDieuTriDTO;
import vn.ehealth.cdr.model.dto.YlenhThuocDTO;
import vn.ehealth.cdr.service.CanboYteService;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.DieuTriService;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/ylenh")
public class YlenhController {
    
    @Autowired private EncounterDao encounterDao;
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private MedicationRequestHelper medicationRequestHelper;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private YlenhService ylenhService;
    @Autowired private DieuTriService dieuTriService;
    @Autowired private DonThuocService donThuocService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private CanboYteService canboYteService;
    
    @PostMapping("/create_ylenh_dieutri/{encounterId}")
    public ResponseEntity<?> createYlenhDieuTri(@PathVariable String encounterId, @RequestBody YlenhDieuTriDTO body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var canboYteId = user != null? user.canBoYteId : null;
            CanboYte canboYte = null;
            
            if(canboYteId != null) {
                canboYte = canboYteService.getById(new ObjectId(canboYteId)).orElse(null);
            }
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            if(hsba.chanDoan == null) {
                hsba.chanDoan = new HoSoBenhAn.ChanDoan();
            }
            
            var ylenh = body.generateYlenh();
            ylenh.idhis = StringUtil.generateUUID();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiRaYlenh = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayRaYlenh = new Date();
            ylenh = ylenhService.save(ylenh);
            
            var dieuTri = body.generateDieuTri();
            
            if(dieuTri.dmMaBenhChanDoan != null) {                
                hsba.chanDoan.dmMaBenhChanDoanDieuTriChinh = dieuTri.dmMaBenhChanDoan;
                hsba = hoSoBenhAnService.save(hsba);
            }            
            
            dieuTri = dieuTriService.createOrUpdateDieuTri(ylenh, dieuTri);
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PostMapping("/create_ylenh_thuoc/{encounterId}")
    public ResponseEntity<?> createYlenhThuoc(@PathVariable String encounterId, @RequestBody YlenhThuocDTO body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var canboYteId = user != null? user.canBoYteId : null;
            CanboYte canboYte = null;
            
            if(canboYteId != null) {
                canboYte = canboYteService.getById(new ObjectId(canboYteId)).orElse(null);
            }
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            if(hsba.chanDoan == null) {
                hsba.chanDoan = new HoSoBenhAn.ChanDoan();
            }
            
            hsba = hoSoBenhAnService.save(hsba);
       
            var ylenh = body.generateYlenh();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiRaYlenh = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayRaYlenh = new Date();
            ylenh = ylenhService.save(ylenh);
            
            var donThuoc = body.generateDonThuoc();
            donThuoc = donThuocService.createOrUpdate(ylenh, donThuoc);
            medicationRequestHelper.saveToFhirDb(hsba, donThuoc);
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PostMapping("/create_ylenh_dvkt/{encounterId}")
    public ResponseEntity<?> createYlenhDVKT(@PathVariable String encounterId, @RequestBody YlenhDVKTDTO body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var canboYteId = user != null? user.canBoYteId : null;
            CanboYte canboYte = null;
            
            if(canboYteId != null) {
                canboYte = canboYteService.getById(new ObjectId(canboYteId)).orElse(null);
            }
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            if(hsba.chanDoan == null) {
                hsba.chanDoan = new HoSoBenhAn.ChanDoan();
                hsba = hoSoBenhAnService.save(hsba);
            }            
       
            var ylenh = body.generateYlenh();
            ylenh.idhis = StringUtil.generateUUID();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiRaYlenh = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayRaYlenh = new Date();
            ylenh = ylenhService.save(ylenh);
            
            var dsDvkt = body.generateDsDichVuKyThuat();
            for(var dvkt : dsDvkt) {
                dvkt = dichVuKyThuatService.createOrUpdate(ylenh, dvkt);
                procedureHelper.saveToFhirDb(ylenh, dvkt);
            }
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/get_list/{encounterId}")
    public ResponseEntity<?> getList(@PathVariable String encounterId) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            var lst = ylenhService.getByHoSoBenhAnId(hsba.id);
            return ResponseEntity.ok(lst);
            
        }catch(Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    @GetMapping("/get_detail/{ylenhId}")
    public ResponseEntity<?> getDetail(@PathVariable String ylenhId) {
        try {
            var ylenh = ylenhService.getById(new ObjectId(ylenhId)).get();
            var dsDVKT = dichVuKyThuatService.getByYlenhId(ylenh.id);
            
            var dsDonThuoc = donThuocService.getByYlenhId(ylenh.id);                      
            var donThuoc = dsDonThuoc.size() > 0? dsDonThuoc.get(0) : null;
            
            var dsDieuTri = dieuTriService.getByYlenhId(ylenh.id);
            var dieuTri = dsDieuTri.size() > 0 ? dsDieuTri.get(0) : null;
            
            var result = mapOf("ylenh", ylenh, "dsDVKT", dsDVKT, "donThuoc", donThuoc, "dieuTri", dieuTri);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
