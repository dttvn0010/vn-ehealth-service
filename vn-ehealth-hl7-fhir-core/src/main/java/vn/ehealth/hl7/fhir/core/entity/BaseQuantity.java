package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseQuantity extends BaseSimpleType  {
    public BigDecimal value;
    public String comparator;
    public String unit;
    public String system;
    public String code;    

}
