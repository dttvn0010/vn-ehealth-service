package vn.ehealth.cdr.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

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
import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.service.CanboYteService;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.DieuTriService;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

@RestController
@RequestMapping("/api/ylenh")
public class YlenhController {

    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private EncounterDao encounterDao;
    
    @Autowired private YlenhService ylenhService;
    @Autowired private DieuTriService dieuTriService;
    @Autowired private DonThuocService donThuocService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private CanboYteService canboYteService;
    
    @PostMapping("/create_ylenh_dieutri/{encounterId}")
    public ResponseEntity<?> createYlenhDieuTri(@PathVariable String encounterId, @RequestBody DieuTri dieuTri) {
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
            
            if(dieuTri.dmMaBenhChanDoan != null) {                
                hsba.chanDoan.dmMaBenhChanDoanDieuTriChinh = dieuTri.dmMaBenhChanDoan;
            }
            
            if(dieuTri.dsDmMaBenhChanDoanKemTheo != null) {
                hsba.chanDoan.dsDmMaBenhChanDoanDieuTriKemTheo = dieuTri.dsDmMaBenhChanDoanKemTheo;
            }
            
            hsba = hoSoBenhAnService.save(hsba);
       
            var ylenh = new Ylenh();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiThucHien = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayThucHien = new Date();
            ylenh = ylenhService.save(ylenh);
            
            dieuTri.ylenhRef = Ylenh.toEmrRef(ylenh);
            dieuTri.bacSiDieuTri = ylenh.bacSiThucHien;
            dieuTri.ngayDieuTri = ylenh.ngayThucHien;
            dieuTri = dieuTriService.save(dieuTri);
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PostMapping("/create_ylenh_thuoc/{encounterId}")
    public ResponseEntity<?> createYlenhThuoc(@PathVariable String encounterId, @RequestBody DonThuoc donThuoc) {
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
       
            var ylenh = new Ylenh();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiThucHien = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayThucHien = new Date();
            ylenh = ylenhService.save(ylenh);
            
            donThuoc.ylenhRef = Ylenh.toEmrRef(ylenh);
            donThuoc.bacSiKeDon = ylenh.bacSiThucHien;
            donThuoc.ngayKeDon = ylenh.ngayThucHien;
            donThuoc = donThuocService.save(donThuoc);
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @PostMapping("/create_ylenh_dvkt/{encounterId}")
    public ResponseEntity<?> createYlenhDVKT(@PathVariable String encounterId, @RequestBody DichVuKyThuat dvkt) {
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
       
            var ylenh = new Ylenh();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiThucHien = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayThucHien = new Date();
            ylenh = ylenhService.save(ylenh);
            
            dvkt.ylenhRef = Ylenh.toEmrRef(ylenh);
            dvkt.bacSiYeuCau = ylenh.bacSiThucHien;
            dvkt.ngayYeuCau = ylenh.ngayThucHien;
            dvkt = dichVuKyThuatService.save(dvkt);
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/get_ds_ylenh/{encounterId}")
    public ResponseEntity<?> getDsYlenh(@PathVariable String encounterId) {
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
}
