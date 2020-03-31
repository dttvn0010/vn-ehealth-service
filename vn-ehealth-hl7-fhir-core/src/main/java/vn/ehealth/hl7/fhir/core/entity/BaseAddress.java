package vn.ehealth.hl7.fhir.core.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.utils.EntityUtils;


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
    
    public Map<String, Object> getDto(Map<String, Object> options) {
        var simple = false;
        
        if(options != null && options.get("simple") != null) {
            simple = (Boolean) options.get("simple");
        }
        
        var dto = new HashMap<String, Object>();
        dto.put("text", text);
        
        if(!simple && extension != null) {
            var extDvhc = EntityUtils.findSimpleExtensionByURL(extension, ExtensionURL.DVHC);
            
            if(extDvhc != null && extDvhc.extension != null) {
                var extCity = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "city");
                if(extCity != null && extCity.value instanceof BaseCodeableConcept) {
                    var cityConcept = (BaseCodeableConcept) extCity.value;
                    dto.put("city", BaseCodeableConcept.toDto(cityConcept));
                }
                
                var extDistrict = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "district");
                if(extDistrict != null && extDistrict.value instanceof BaseCodeableConcept) {
                    var districtConcept = (BaseCodeableConcept) extDistrict.value;
                    dto.put("district", BaseCodeableConcept.toDto(districtConcept));
                }
                
                var extWard = EntityUtils.findRawSimpleExtensionByURL(extDvhc.extension, "ward");
                if(extWard != null && extWard.value instanceof BaseCodeableConcept) {
                    var wardConcept = (BaseCodeableConcept) extWard.value;
                    dto.put("ward", BaseCodeableConcept.toDto(wardConcept));
                }
            }
        }
        return dto;
    }
    
    public static Map<String, Object> toDto(BaseAddress addr, Map<String, Object> options) {
        if(addr == null) return null;
        return addr.getDto(options);
    }
    
    public static Map<String, Object> toDto(BaseAddress addr) {
        return toDto(addr, null);
    }
}
