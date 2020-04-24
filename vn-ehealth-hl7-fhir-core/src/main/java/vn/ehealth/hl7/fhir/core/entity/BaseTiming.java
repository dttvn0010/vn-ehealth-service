package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;;

@JsonInclude(Include.NON_NULL)
public class BaseTiming extends BaseBackboneType {
    
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
