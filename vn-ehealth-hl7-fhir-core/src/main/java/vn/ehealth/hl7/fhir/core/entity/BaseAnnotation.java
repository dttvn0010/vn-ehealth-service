package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
public class BaseAnnotation extends BaseComplexType {
    public BaseSimpleType author;
    public Date time;
    public String text;    
}
