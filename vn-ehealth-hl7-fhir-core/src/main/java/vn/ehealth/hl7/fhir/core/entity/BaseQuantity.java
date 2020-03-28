package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

public class BaseQuantity extends BaseType {
    public BigDecimal value;
    public String comparator;
    public String unit;
    public String system;
    public String code;
}
