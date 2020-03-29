package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

public class BaseCodeableConcept  {
    public List<BaseCoding> coding;
    public String text;
    
    public String getCode() {
        if(coding != null && coding.size() > 0) {
            return coding.get(0).code;
        }
        return "";
    }
    
    public String getText() {
        if(coding != null && coding.size() > 0) {
            return coding.get(0).display;
        }
        return text;
    }

}
