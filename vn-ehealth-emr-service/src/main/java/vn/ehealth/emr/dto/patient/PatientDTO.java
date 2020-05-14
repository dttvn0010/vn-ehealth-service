package vn.ehealth.emr.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.Patient;

import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class PatientDTO extends PatientEntity {   
    
    public static PatientDTO fromFhir(Patient obj) {
        return fhirToEntity(obj, PatientDTO.class);        
    }
}
