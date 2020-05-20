package vn.ehealth.emr.dto.diagnostic;

import org.hl7.fhir.r4.model.Observation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.ObservationEntity;

@JsonInclude(Include.NON_NULL)
public class ObservationDTO extends ObservationEntity{
    
    public static ObservationDTO fromFhir(Observation obj) {
        
    	return DataConvertUtil.fhirToEntity(obj, ObservationDTO.class);
    }
    
    public static Observation toFhir(ObservationDTO dto) {
    	return DataConvertUtil.entityToFhir(dto, Observation.class);
    }
}
