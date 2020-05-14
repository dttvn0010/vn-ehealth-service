package vn.ehealth.emr.dto.provider;

import org.hl7.fhir.r4.model.Practitioner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;

@JsonInclude(Include.NON_NULL)
public class PractitionerDTO extends PractitionerEntity {

    public static PractitionerDTO fromFhir(Practitioner obj) {
        
        return DataConvertUtil.fhirToEntity(obj, PractitionerDTO.class);
    }
}
