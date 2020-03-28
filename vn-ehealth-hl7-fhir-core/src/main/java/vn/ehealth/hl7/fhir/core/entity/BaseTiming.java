package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;;

public class BaseTiming extends BaseType {
    
    public static class BaseTimingRepeat extends BaseType {
        @JsonIgnore public Type bounds;
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
