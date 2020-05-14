package vn.ehealth.emr.dto.provider;

import org.hl7.fhir.r4.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.OrganizationEntity;

@JsonInclude(Include.NON_NULL)
public class OrganizationDTO extends OrganizationEntity {

    public static OrganizationDTO fromFhir(Organization obj) {
    	return DataConvertUtil.fhirToEntity(obj, OrganizationDTO.class);
    }
}
