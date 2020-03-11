package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseNotAvailableTime{
    public String description; 
    public BasePeriod during;

    public static BaseNotAvailableTime fromHealthcareServiceNotAvailableComponent(HealthcareServiceNotAvailableComponent object) {
        if(object == null) return null;
                
        var entity = new BaseNotAvailableTime();
        entity.description = object.getDescription();
        entity.during = BasePeriod.fromPeriod(object.getDuring());
        
        return entity;
    }
    
    public static List<BaseNotAvailableTime> fromHealthcareServiceNotAvailableComponent(List<HealthcareServiceNotAvailableComponent> lst) {
        return transform(lst, x -> fromHealthcareServiceNotAvailableComponent(x));
        
    }
    
    public static HealthcareServiceNotAvailableComponent toHealthcareServiceNotAvailableComponent(BaseNotAvailableTime entity) {
        if(entity == null) return null;
        
        var object = new HealthcareServiceNotAvailableComponent();
        object.setDescription(entity.description);
        object.setDuring(BasePeriod.toPeriod(entity.during));
        
        return object;
    }
    
    public static List<HealthcareServiceNotAvailableComponent> toHealthcareServiceNotAvailableComponent(List<BaseNotAvailableTime> entityList) {
        return transform(entityList, x -> toHealthcareServiceNotAvailableComponent(x));
    }
}
