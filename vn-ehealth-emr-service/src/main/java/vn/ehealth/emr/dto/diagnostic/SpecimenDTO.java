package vn.ehealth.emr.dto.diagnostic;

import org.hl7.fhir.r4.model.Specimen;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.SpecimenEntity;

@JsonInclude(Include.NON_NULL)
public class SpecimenDTO extends SpecimenEntity {
   
    
    public static SpecimenDTO fromFhir(Specimen obj) {
        return DataConvertUtil.fhirToEntity(obj, SpecimenDTO.class);
        
    }
}
