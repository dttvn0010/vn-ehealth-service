package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;

public class BaseSampledData extends BaseComplexType {

    public BaseQuantity origin;
    public BigDecimal period;
    public BigDecimal factor;
    public BigDecimal lowerLimit;
    public BigDecimal upperLimit;
    public Integer dimensions;
    public String data;
}
