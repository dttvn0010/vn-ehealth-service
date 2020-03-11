package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek;
import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseAvailableTime {
    public List<String> daysOfWeek;
    public Boolean allDay;
    public String availableStartTime;
    public String availableEndTime;

   
    public static BaseAvailableTime fromHealthcareServiceAvailableTimeComponent(HealthcareServiceAvailableTimeComponent object) {
        if(object == null) return null;
        
        var entity = new BaseAvailableTime();
        entity.daysOfWeek = transform(object.getDaysOfWeek(), x -> x.getValueAsString());
        entity.allDay = object.getAllDay();
        entity.availableStartTime = object.getAvailableStartTime();
        entity.availableEndTime = object.getAvailableEndTime();
        return entity;
    }
    
    public static List<BaseAvailableTime> fromHealthcareServiceAvailableTimeComponent(List<HealthcareServiceAvailableTimeComponent> lst) {
        return transform(lst, x -> fromHealthcareServiceAvailableTimeComponent(x));        
    }
    
    public static HealthcareServiceAvailableTimeComponent toHealthcareServiceAvailableTimeComponent(BaseAvailableTime entity) {
        if(entity == null) return null;
        
        var object = new HealthcareServiceAvailableTimeComponent();
        if(entity.daysOfWeek != null) {
            entity.daysOfWeek.forEach(x -> {
                object.addDaysOfWeek(DaysOfWeek.fromCode(x.toLowerCase()));
            });
        }
        object.setAvailableStartTime(entity.availableStartTime);
        object.setAvailableEndTime(entity.availableEndTime);
        return object;
    }
    
    public static List<HealthcareServiceAvailableTimeComponent> toHealthcareServiceAvailableTimeComponent(List<BaseAvailableTime> entityList) {
        return transform(entityList, x -> toHealthcareServiceAvailableTimeComponent(x));
        
    }
}
