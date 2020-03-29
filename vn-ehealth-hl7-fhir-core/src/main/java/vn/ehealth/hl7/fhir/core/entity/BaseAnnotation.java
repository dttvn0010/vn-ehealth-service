package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAnnotation extends BaseComplexType {
    public BaseSimpleType author;
    public Date time;
    public String text;    
}
