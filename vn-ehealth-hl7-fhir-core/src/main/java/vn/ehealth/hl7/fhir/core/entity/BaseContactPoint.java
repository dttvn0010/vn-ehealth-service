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
   
    public static BaseContactPoint fromContactPoint(ContactPoint obj) {        
        if(obj == null) return null;
        
        var ent = new BaseContactPoint();
        ent.system = obj.hasSystem()? obj.getSystem().toCode() : null;
        ent.value = obj.hasValue()? obj.getValue() : null;
        ent.use = obj.hasUse()? obj.getUse().toCode() : null;        
        ent.rank = obj.hasRank()? obj.getRank() : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        return ent;
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
        if(entity.rank != null) object.setRank(entity.rank);
        object.setPeriod(BasePeriod.toPeriod(entity.period));
        return object;
    }

    public static List<ContactPoint> toContactPointList(List<BaseContactPoint> entityList) {
        return transform(entityList, x -> toContactPoint(x));        
    }
}
