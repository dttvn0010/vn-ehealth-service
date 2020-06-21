package vn.ehealth.cdr.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.service.CanboYteService;
import vn.ehealth.cdr.service.ChamSocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/api/chamsoc")
public class ChamSocController {
    
    @Autowired private EncounterDao encounterDao;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private ChamSocService chamSocService;
    @Autowired private CanboYteService canboYteService;
    
    @PostMapping("/create_chamsoc/{encounterId}")
    public ResponseEntity<?> createChamSoc(@PathVariable String encounterId, @RequestBody ChamSoc chamSoc) {
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
            
            chamSoc.idhis = StringUtil.generateUUID();
            chamSoc.ytaChamSoc = CanboYteDTO.fromCanboYte(canboYte);
            chamSoc.ngayChamSoc = new Date();
            
            chamSoc = chamSocService.createOrUpdate(hsba, chamSoc);
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    
    @GetMapping("/get_ds_chamsoc")
    public ResponseEntity<?> getDsChamSoc(@RequestParam("hsba_id") String id) {
    
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    
}
