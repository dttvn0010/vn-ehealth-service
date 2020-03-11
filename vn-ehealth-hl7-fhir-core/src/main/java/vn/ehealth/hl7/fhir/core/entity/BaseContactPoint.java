package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseContactPoint {

    public String system;
    public String value;
    public String use;
    public Integer rank;
    public BasePeriod period;
   
    public static BaseContactPoint fromContactPoint(ContactPoint object) {
        if(object == null) return null;
        
        var entity = new BaseContactPoint();
        
        if(object.getSystem() != null) {
            entity.system = object.getSystem().toCode();
        }
        
        entity.value = object.getValue();
        
        if(object.getUse() != null) {
            entity.use = object.getUse().toCode();
        }
        
        entity.rank = object.getRank();
        entity.period = BasePeriod.fromPeriod(object.getPeriod());
        return entity;
    }
    
    
    public static List<BaseContactPoint> fromContactPointList(List<ContactPoint> lst) {
        return transform(lst, x -> fromContactPoint(x));        
    }
    
    public static ContactPoint toContactPoint(BaseContactPoint entity) {
        if(entity == null) return null;
        
        var object = new ContactPoint();
        object.setSystem(ContactPointSystem.fromCode(entity.system));
        object.setValue(entity.value);
        object.setUse(ContactPointUse.fromCode(entity.use));
        object.setRank(entity.rank);
        object.setPeriod(BasePeriod.toPeriod(entity.period));
        return object;
    }

    public static List<ContactPoint> toContactPointList(List<BaseContactPoint> entityList) {
        return transform(entityList, x -> toContactPoint(x));        
    }
}
