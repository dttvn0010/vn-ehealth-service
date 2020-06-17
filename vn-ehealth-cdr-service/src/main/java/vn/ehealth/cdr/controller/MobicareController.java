package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.Date;

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
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

@RestController
@RequestMapping("/api/mobicare")
public class MobicareController {

    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private EncounterDao encounterDao;
    @Autowired private PractitionerDao practitionerDao;
    
    @Autowired private YlenhService ylenhService;
    
    @PostMapping("/create_ylenh/{encounterId}")
    public ResponseEntity<?> createYlenh(@PathVariable String encounterId, @RequestBody Ylenh ylenh) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var practitionerId = user != null? user.fhirPractitionerId : null;
            var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            ylenh.hoSoBenhAnId = hsba.id;
            ylenh.benhNhanId = hsba.benhNhanId;
            ylenh.coSoKhamBenhId = hsba.coSoKhamBenhId;
            
            ylenh.ngayDieuTri = new Date();
            ylenh.bacSiRaYlenh =  CanboYte.fromFhir(practitioner);
            ylenh = ylenhService.save(ylenh);
            
            return ResponseEntity.ok(mapOf("success", true, "ylenh", ylenh));
            
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
