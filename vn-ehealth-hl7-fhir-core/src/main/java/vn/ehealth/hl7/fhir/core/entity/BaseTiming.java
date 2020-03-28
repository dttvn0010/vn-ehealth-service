package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;;

public class BaseTiming {
    
    public static class BaseTimingRepeat {
        @JsonIgnore public Type bounds;
        public Integer count;
        public Integer countMax;
        public List<String> dayOfWeek;
        public BigDecimal duration;
        public BigDecimal durationMax;
        public String durationUnit;
        public Integer frequency;
        public Integer frequencyMax;
        public Integer offset;
        public BigDecimal period;
        public BigDecimal periodMax;
        public String periodUnit;
        public List<String> timeOfDay;
        public List<String> when;
        public List<Extension> extension;
        
    }
    
    public BaseCodeableConcept code;
    public List<Date> event;
    public BaseTimingRepeat repeat;
    public List<Extension> extension;
}
