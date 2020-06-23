package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.DonThuocChiTietService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

@RestController
@RequestMapping("/api/donthuoc_chitiet")
public class DonThuocChiTietController {
    @Autowired private EncounterDao encounterDao;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private DonThuocChiTietService donThuocChiTietService;

	@GetMapping("/get_ds_uongthuoc_ngay/{encounterId}")
    public ResponseEntity<?> getDsUongThuoc(@PathVariable String encounterId,
                            @RequestParam Optional<Integer> start,
                            @RequestParam Optional<Integer> count) {
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
	            
	            var date = new Date();
	            var dsDonThuocChiTiet = donThuocChiTietService.getByNgayUongThuoc(hsba.id, date,
	                                        start.orElse(-1), count.orElse(-1));
	            
	            return ResponseEntity.ok(dsDonThuocChiTiet);
	        }catch(Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.ok(new ArrayList<>());
	        }
	}
    
}
