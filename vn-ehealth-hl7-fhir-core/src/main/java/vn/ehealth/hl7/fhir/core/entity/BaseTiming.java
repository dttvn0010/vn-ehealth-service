package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;;

public class BaseTiming extends BaseComplexType {
    
    public static class BaseTimingRepeat {
        public BaseSimpleType bounds;
        public Integer count;
        public Integer countMax;
        public BigDecimal duration;
        public BigDecimal durationMax;
        public String durationUnit;
        public Integer frequency;
        public Integer frequencyMax;
        public BigDecimal period;
        public BigDecimal periodMax;
        public String periodUnit;
        public List<String> dayOfWeek;
        public List<String> timeOfDay;
        public List<String> when;
        public Integer offset;
    }
    
    public List<Date> event;
    public BaseTimingRepeat repeat;
    public BaseCodeableConcept code;
    
    
}
