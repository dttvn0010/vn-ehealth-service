package vn.ehealth.emr.dto;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;

@JsonInclude(Include.NON_NULL)
public class AddressDTO {

    public String text;
    public CodeableConceptDTO city;
    public CodeableConceptDTO district;
    public CodeableConceptDTO ward;
    
    public static AddressDTO fromFhir(Address obj) {
        if(obj == null) return null;
        
        var dto = new AddressDTO();
        
        dto.text = obj.getText();

        var extension = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.DVHC);
       
        if(extension != null && extension.hasExtension()) {
            var ext = FhirUtil.findExtensionByURL(extension.getExtension(), "city");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.city = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
            }
            
            ext = FhirUtil.findExtensionByURL(extension.getExtension(), "district");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.district = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
            }
            
            ext = FhirUtil.findExtensionByURL(extension.getExtension(), "ward");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.ward = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
            }
        }
        
        return dto;
    }
}
