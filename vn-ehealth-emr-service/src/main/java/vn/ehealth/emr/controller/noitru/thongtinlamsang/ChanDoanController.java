package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.PositiveIntType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.clinical.ConditionDTO;
import vn.ehealth.emr.dto.ehr.EncounterDTO.DiagnosisDTO;
import vn.ehealth.emr.utils.EmrConstants.DiagnosisRole;
import vn.ehealth.emr.utils.EmrUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	
	
	private Map<String, List<String>> validateForm(ChanDoanBody body, String conditionId, Encounter enc) {
	    var errors = new HashMap<String, List<String>>();
	    
	    if(body.maICD10 == null || StringUtils.isBlank(body.maICD10.code)) {
	        EmrUtils.addError(errors, "maICD10", MessageUtils.get("validate.required"));
	    }
	    
	    if(body.loaiChanDoan == null || StringUtils.isBlank(body.loaiChanDoan.code)) {
            EmrUtils.addError(errors, "loaiChanDoan", MessageUtils.get("validate.required"));
        }
	    
	    if(body.loaiChanDoan != null && DiagnosisRole.EDA.equals(body.loaiChanDoan.code)) {
	        if(body.rank == null) {
	            EmrUtils.addError(errors, "rank", MessageUtils.get("validate.required"));	            
	        }
	    }
	    
	    if(body.loaiChanDoan != null) {
	        
    	    for(var diagnosis: enc.getDiagnosis()) {
    	        var useCode = diagnosis.getUse().getCodingFirstRep().getCode();
    	        
    	        if(useCode == null || DiagnosisRole.DDA.equals(useCode) || DiagnosisRole.EDA.equals(useCode)) {
    	            continue;
    	        }
    	        
    	        if(diagnosis.getCondition().getReference().endsWith("/" + conditionId)) {
    	            continue;
    	        }
    	        
    	        if(useCode.equals(body.loaiChanDoan.code)) {
    	            EmrUtils.addError(errors, "loaiChanDoan", MessageUtils.get("diagnosis.role.already.exist"));
    	        }
    	    }
	    }
	    
	    return errors;
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> addChanDoan(@RequestParam String encounterId,  @RequestBody ChanDoanBody body) {
		try {
		    var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
		    
		    var errors = validateForm(body, null, encounter);
		    if(errors.size() > 0) {
		        return ResponseEntity.ok(mapOf("success", false, "errors", errors));
		    }			
			
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
			
			if(DiagnosisRole.EDA.equals(body.loaiChanDoan.code)) {
				diagnosis.setRank(body.rank);
			}
			
			diagnosis.setUse(CodingDTO.toCodeableConcept(body.loaiChanDoan,  CodeSystemValue.DIAGNOSIS_ROLE));
			
			encounterDao.update(encounter, encounter.getIdElement());
			
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}		
	}
	
	@PutMapping("/update")
    public ResponseEntity<?> updateChanDoan(@RequestParam String encounterId, 
                            @RequestParam String conditionId,
                            @RequestBody ChanDoanBody body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            
            var errors = validateForm(body, conditionId, encounter);
            if(errors.size() > 0) {
                return ResponseEntity.ok(mapOf("success", false, "errors", errors));
            }            
            
            var user = UserUtil.getCurrentUser().orElse(null);
            var practitionerId = user != null? user.fhirPractitionerId : null;
            var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
                    
            
            var diagnosis = FPUtil.findFirst(encounter.getDiagnosis(),
                                x -> x.getCondition().getReference().endsWith("/" + conditionId));
            
            if(diagnosis != null) {
                var condition = conditionDao.read(FhirUtil.createIdType(conditionId));
                condition.setCode(CodingDTO.toCodeableConcept(body.maICD10, CodeSystemValue.ICD10));
                
                var practitionerRef = FhirUtil.createReference(practitioner);
                practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
                condition.setRecorder(practitionerRef);
                condition.setRecordedDate(new Date());
                condition = conditionDao.update(condition, condition.getIdElement()); 
                
                if(DiagnosisRole.EDA.equals(body.loaiChanDoan.code)) {
                    diagnosis.setRank(body.rank);
                }else {
                    diagnosis.setRankElement(new PositiveIntType());
                }
                
                diagnosis.setUse(CodingDTO.toCodeableConcept(body.loaiChanDoan,  CodeSystemValue.DIAGNOSIS_ROLE));
                
                encounterDao.update(encounter, encounter.getIdElement());
                
                return ResponseEntity.ok(mapOf("success", true));
            }else {
                return ResponseEntity.ok(mapOf("success", false, "error", "No diagnosis found"));
            }
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }       
    }
}
