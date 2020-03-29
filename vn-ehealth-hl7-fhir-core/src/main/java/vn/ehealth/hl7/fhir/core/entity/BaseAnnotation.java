package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAnnotation extends BaseComplexType {
    @JsonIgnore public Object author;
    public Date time;
    public String text;    
}
