package vn.ehealth.emr.dto.ehr;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.constants.EmrConstants.ObservationCodes.ChucNangSong;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.emr.helper.HelperFactory;
import vn.ehealth.emr.utils.EmrConstants.DiagnosisRole;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class EncounterDTO extends EncounterEntity {
    
    
	public static class DiagnosisDTO extends EncounterEntity.Diagnosis {
		public static DiagnosisDTO fromFhir(Encounter.DiagnosisComponent obj) {
			return fhirToEntity(obj, DiagnosisDTO.class);
		}
	}
	
	private static CodingDTO getEDCondition(Encounter enc) {
        var ed = FPUtil.findFirst(enc.getDiagnosis(), 
                    x -> DiagnosisRole.ED.equals(x.getUse().getCodingFirstRep().getCode()));
        if(ed != null && ed.hasCondition()) {
            return CodingDTO.fromText(ed.getCondition().getDisplay());
        }
        
        return null;
    }
    
    private static List<CodingDTO> getEDACondition(Encounter enc) {
        var lst = new ArrayList<CodingDTO>();
        for(var diagnosis : enc.getDiagnosis()) {
            if(DiagnosisRole.EDA.equals(diagnosis.getUse().getCodingFirstRep().getCode())) {
                if(diagnosis.hasCondition()) {
                    String conditionDisplay = diagnosis.getCondition().getDisplay(); 
                    lst.add(CodingDTO.fromText(conditionDisplay));
                }
            }
        }
        return lst;
    }
    
    private Observation getLastObservation(String observationCode) {
        return HelperFactory.getObservationHelper().getLastObservation(this._fhirId, observationCode);
    }
    
    public static EncounterDTO fromFhir(Encounter obj) {
        var dto =  fhirToEntity(obj, EncounterDTO.class);
        if(dto != null) {
        	var medicalRecord = FhirUtil.findIdentifierBySystem(obj.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
        	if(medicalRecord != null) {
        	    dto.computes.put("medicalRecord", medicalRecord.getValue());
        	}
        }
        
        // Chan doan
        dto.computes.put("edCondition", getEDCondition(obj));
        dto.computes.put("edaCondition", getEDACondition(obj));
        
        // Chung nang song
        dto.computes.put("heartRateObs", ObservationDTO.fromFhir(dto.getLastObservation(ChucNangSong.HEART_RATE)));
        dto.computes.put("bpSystolicObs", ObservationDTO.fromFhir(dto.getLastObservation(ChucNangSong.BP_SYSTOLIC)));
        dto.computes.put("bpDiastolicObs", ObservationDTO.fromFhir(dto.getLastObservation(ChucNangSong.BP_DIASTOLIC)));
        dto.computes.put("respirationRateObs", ObservationDTO.fromFhir(dto.getLastObservation(ChucNangSong.RESPIRATION_RATE)));
        dto.computes.put("bodyTemperatureObs", ObservationDTO.fromFhir(dto.getLastObservation(ChucNangSong.BODY_TEMPERATURE)));
        
        return dto;
    }    
    
}
