package vn.ehealth.emr.dto.ehr;

import org.hl7.fhir.r4.model.Encounter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
public class EncounterDTO extends EncounterEntity {
    
    public static EncounterDTO fromFhir(Encounter obj) {
        return  fhirToEntity(obj, EncounterDTO.class);
    }    
    
}
