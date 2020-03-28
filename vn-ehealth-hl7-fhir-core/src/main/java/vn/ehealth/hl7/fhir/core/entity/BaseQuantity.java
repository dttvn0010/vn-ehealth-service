package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseQuantity {
    public BigDecimal value;
    public String comparator;
    public String unit;
    public String system;
    public String code;
    public List<Extension> extension;
}
