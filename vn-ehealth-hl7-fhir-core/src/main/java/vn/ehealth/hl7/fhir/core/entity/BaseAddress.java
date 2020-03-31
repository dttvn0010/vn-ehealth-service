package vn.ehealth.hl7.fhir.core.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.view.DTOView;
import vn.ehealth.hl7.fhir.utils.EntityUtils;

import com.fasterxml.jackson.annotation.JsonView;


/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
public class BaseAddress extends BaseComplexType {    
    public String use;
    public String type;
    public String text;    
    public List<String> line;
    public String city;
    public String district;    
    public String state;
    public String postalCode;
    public String country;
    public BasePeriod period;
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        var dto = new HashMap<String, Object>();
        dto.put("text", text);
        if(extension != null) {
            var extDvhc = EntityUtils.findSimpleExtensionByURL(extension, ExtensionURL.DVHC);
            
            if(extDvhc != null && extDvhc.extension != null) {
                var extCity = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "city");
                if(extCity != null && extCity.value instanceof BaseCodeableConcept) {
                    dto.put("city", ((BaseCodeableConcept)extCity.value).getDto());
                }
                
                var extDistrict = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "district");
                if(extDistrict != null && extDistrict.value instanceof BaseCodeableConcept) {
                    dto.put("district", ((BaseCodeableConcept)extDistrict.value).getDto());
                }
                
                var extWard = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "ward");
                if(extWard != null && extWard.value instanceof BaseCodeableConcept) {
                    dto.put("ward", ((BaseCodeableConcept)extWard.value).getDto());
                }
            }
        }
        return dto;
    }
    
    public static Map<String, Object> toDto(BaseAddress addr) {
        if(addr == null) return null;
        return addr.getDto();
    }
}
