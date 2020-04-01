package vn.ehealth.emr.dto.provider;

import org.hl7.fhir.r4.model.Organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.AddressDTO;
import vn.ehealth.emr.dto.BaseDTO;

@JsonInclude(Include.NON_NULL)
public class OrganizationDTO extends BaseDTO {

    public String name;
    public AddressDTO address;
    
    public static OrganizationDTO fromFhir(Organization obj) {
        if(obj == null) return null;
        
        var dto = new OrganizationDTO();
        
        dto.id = obj.getId();
        dto.name = obj.getName();
        dto.address = AddressDTO.fromFhir(obj.getAddressFirstRep());
        
        return dto;
    }
}
