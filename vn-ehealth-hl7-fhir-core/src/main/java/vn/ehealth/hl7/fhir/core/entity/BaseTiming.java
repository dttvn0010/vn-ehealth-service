package vn.ehealth.hl7.fhir.core.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Timing;
import org.hl7.fhir.r4.model.Timing.DayOfWeek;
import org.hl7.fhir.r4.model.Timing.EventTiming;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.r4.model.Timing.UnitsOfTime;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;;

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
        
        public static TimingRepeatComponent toTimingRepeatComponent(BaseTimingRepeat ent) {
            if(ent == null) return null;
            var obj = new TimingRepeatComponent();
            obj.setBounds(ent.bounds);
            if(ent.count != null) obj.setCount(ent.count);
            if(ent.countMax != null) obj.setCountMax(ent.countMax);
            
            if(ent.dayOfWeek != null) {
                ent.dayOfWeek.forEach(x -> {
                    obj.addDayOfWeek(DayOfWeek.fromCode(x.toLowerCase()));
                });
            }
            obj.setDuration(ent.duration);
            obj.setDurationMax(ent.durationMax);
            obj.setDurationUnit(UnitsOfTime.fromCode(ent.durationUnit));
            if(ent.frequency != null) obj.setFrequency(ent.frequency);
            if(ent.frequencyMax != null) obj.setFrequencyMax(ent.frequencyMax);
            if(ent.offset != null) obj.setOffset(ent.offset);
            obj.setPeriod(ent.period);
            obj.setPeriodMax(ent.periodMax);
            obj.setPeriodUnit(UnitsOfTime.fromCode(ent.periodUnit));
            obj.setTimeOfDay(transform(ent.timeOfDay, x -> new TimeType(x)));
            
            if(ent.when != null) {
                ent.when.forEach(x -> {
                    obj.addWhen(EventTiming.fromCode(x));
                });
            }
            return obj;
        }
        
        public static BaseTimingRepeat fromTimingRepeatComponent(TimingRepeatComponent obj) {
            if(obj == null) return null;
            
            var ent = new BaseTimingRepeat();
            ent.bounds = obj.hasBounds()? obj.getBounds() : null;
            ent.count = obj.hasCount()? obj.getCount() : null;
            ent.countMax = obj.hasCountMax()? obj.getCountMax() : null;
            ent.dayOfWeek = obj.hasDayOfWeek()? transform(obj.getDayOfWeek(), x -> x.getValue().toCode()) : null;            
            ent.duration = obj.hasDuration()? obj.getDuration() : null;
            ent.durationMax = obj.hasDurationMax()? obj.getDurationMax() : null;
            ent.durationUnit = obj.hasDurationUnit()?  obj.getDurationUnit().toCode() : null;
            ent.frequency = obj.hasFrequency()? obj.getFrequency() : null;
            ent.frequencyMax = obj.hasFrequencyMax()? obj.getFrequencyMax() : null;
            ent.offset = obj.hasOffset()? obj.getOffset() : null;
            ent.period = obj.hasPeriod()? obj.getPeriod() : null;
            ent.periodMax = obj.hasPeriodMax()? obj.getPeriodMax() : null;
            ent.periodUnit = obj.hasPeriodUnit()? obj.getPeriodUnit().toCode() : null;
            ent.timeOfDay = obj.hasTimeOfDay()? transform(obj.getTimeOfDay(), x -> x.getValue()) : null;
            ent.when = obj.hasWhen()? transform(obj.getWhen(), x -> x.getValue().toCode()) : null;
            
            return ent;
        }
    }
    
    public BaseCodeableConcept code;
    public List<Date> event;
    public BaseTimingRepeat repeat;
    
    public static BaseTiming fromTiming(Timing obj) {
        if(obj == null) return null;
        var ent = new BaseTiming();
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.event = obj.hasEvent()? transform(obj.getEvent(), x -> x.getValue()) : null;
        ent.repeat = obj.hasRepeat()? BaseTimingRepeat.fromTimingRepeatComponent(obj.getRepeat()) : null;
        return ent;
    }
    
    public static Timing toTiming(BaseTiming ent) {
        var obj = new Timing();
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setEvent(transform(ent.event, x -> new DateTimeType(x)));
        obj.setRepeat(BaseTimingRepeat.toTimingRepeatComponent(ent.repeat));        
        return obj;
    }
}
