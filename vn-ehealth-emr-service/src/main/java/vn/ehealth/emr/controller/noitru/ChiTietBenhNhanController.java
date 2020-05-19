package vn.ehealth.emr.controller.noitru;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.dto.ehr.EncounterDTO;
import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

@RestController
@RequestMapping("/api/noitru/chitiet_benhnhan")
public class ChiTietBenhNhanController {

	@Autowired private EncounterDao encounterDao;
		
	@GetMapping("/get_detail/{encounterId}")
	public ResponseEntity<?> getDetail(@PathVariable String encounterId) {
		var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
		DatabaseUtil.setReferenceResource(encounter.getSubject());
		
		var encounterDto = EncounterDTO.fromFhir(encounter);
		var patientDto = PatientDTO.fromFhir((Patient) encounterDto.subject.resource);
		encounterDto.computes.put("patient", patientDto);
		
		return ResponseEntity.ok(encounterDto);
	}
} 
