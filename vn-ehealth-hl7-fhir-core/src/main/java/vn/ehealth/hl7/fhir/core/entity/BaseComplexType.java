package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseComplexType extends BaseType {

    public List<SimpleExtension> extension;
    
    public List<SimpleExtension> getExtension() {
        if(extension != null && extension.size() > 0) {
            return extension;
        }
        return null;
    }
}
