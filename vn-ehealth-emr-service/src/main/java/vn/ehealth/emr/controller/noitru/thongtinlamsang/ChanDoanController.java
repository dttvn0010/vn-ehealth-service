package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import org.hl7.fhir.r4.model.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.clinical.ConditionDTO;
import vn.ehealth.emr.dto.ehr.EncounterDTO.DiagnosisDTO;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ConditionDao;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.ArrayList;
import java.util.Date;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/chandoan")
public class ChanDoanController {
	
	@Autowired private EncounterDao encounterDao;
	@Autowired private PractitionerDao practitionerDao;
	@Autowired private ConditionDao conditionDao;
	
	static class ChanDoanBody {
		public CodingDTO loaiBenh;
		public CodingDTO loaiChanDoan;
		public CodingDTO maICD10;
		public Integer rank;
	}
	
	@GetMapping("/get_list")
	public ResponseEntity<?> getDsChanDoan(@RequestParam String encounterId) {
		var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
		var result = new ArrayList<>();
		
		for(var diagnosis : encounter.getDiagnosis()) {
			var diagnosisDTO = DiagnosisDTO.fromFhir(diagnosis);
			var condition = conditionDao.read(FhirUtil.createIdType(diagnosis.getCondition()));
			diagnosisDTO.computes.put("condition", ConditionDTO.fromFhir(condition));
			result.add(diagnosisDTO);
		}
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/delete")
	public ResponseEntity<?> deleteChanDoan(@RequestParam String encounterId,
								@RequestParam String conditionRef) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			conditionDao.remove(FhirUtil.createIdType(conditionRef));
			var diagnosis = FPUtil.filter(encounter.getDiagnosis(), 
								x -> !conditionRef.equals(x.getCondition().getReference()));
			
			encounter.setDiagnosis(diagnosis);
			encounterDao.update(encounter, encounter.getIdElement());
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> addChanDoan(@RequestParam String encounterId,  @RequestBody ChanDoanBody body) {
		try {
			
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var user = UserUtil.getCurrentUser().orElse(null);
			var practitionerId = user != null? user.fhirPractitionerId : null;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			var condition = new Condition();
			condition.setEncounter(FhirUtil.createReference(encounter));
			condition.setSubject(encounter.getSubject());
			condition.setCode(CodingDTO.toCodeableConcept(body.maICD10, CodeSystemValue.ICD10));
			
			var practitionerRef = FhirUtil.createReference(practitioner);
			practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
			condition.setRecorder(practitionerRef);
			condition.setRecordedDate(new Date());
			condition = conditionDao.create(condition);			
			
			var diagnosis = encounter.addDiagnosis();
			var conditionRef = FhirUtil.createReference(condition);
			conditionRef.setDisplay(FhirUtil.getCodeableConceptDisplay(condition.getCode()));
			diagnosis.setCondition(conditionRef);
			
			if(body.rank != null) {
				diagnosis.setRank(body.rank);
			}
			
			diagnosis.setUse(CodingDTO.toCodeableConcept(body.loaiChanDoan,  CodeSystemValue.DIAGNOSIS_ROLE));
			
			encounterDao.update(encounter, encounter.getIdElement());
			
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}		
	}
}
