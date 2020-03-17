package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseAvailableTime {
    public List<String> daysOfWeek;
    public Boolean allDay;
    public String availableStartTime;
    public String availableEndTime;
    public List<Extension> extension;
   
    public static BaseAvailableTime fromHealthcareServiceAvailableTimeComponent(HealthcareServiceAvailableTimeComponent obj) {
        if(obj == null) return null;
        
        var ent = new BaseAvailableTime();
        ent.daysOfWeek = obj.hasDaysOfWeek()? transform(obj.getDaysOfWeek(), x -> x.getValueAsString()) : null;
        ent.allDay = obj.hasAllDay()? obj.getAllDay() : null;
        ent.availableStartTime = obj.hasAvailableStartTime()? obj.getAvailableStartTime() : null;
        ent.availableEndTime = obj.hasAvailableStartTime()? obj.getAvailableEndTime() : null;
        ent.extension = obj.hasExtension()? obj.getExtension() : null;
        return ent;
    }
    
    public static List<BaseAvailableTime> fromHealthcareServiceAvailableTimeComponent(List<HealthcareServiceAvailableTimeComponent> lst) {
        return transform(lst, x -> fromHealthcareServiceAvailableTimeComponent(x));        
    }
    
    public static HealthcareServiceAvailableTimeComponent toHealthcareServiceAvailableTimeComponent(BaseAvailableTime ent) {
        if(ent == null) return null;
        
        var obj = new HealthcareServiceAvailableTimeComponent();
        if(ent.daysOfWeek != null) {
            ent.daysOfWeek.forEach(x -> {
                obj.addDaysOfWeek(DaysOfWeek.fromCode(x.toLowerCase()));
            });
        }
        if(ent.allDay != null) obj.setAllDay(ent.allDay);
        obj.setAvailableStartTime(ent.availableStartTime);
        obj.setAvailableEndTime(ent.availableEndTime);
        obj.setExtension(ent.extension);
        return obj;
    }
    
    public static List<HealthcareServiceAvailableTimeComponent> toHealthcareServiceAvailableTimeComponent(List<BaseAvailableTime> entityList) {
        return transform(entityList, x -> toHealthcareServiceAvailableTimeComponent(x));
        
    }
}
