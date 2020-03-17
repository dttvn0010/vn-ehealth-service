package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Period;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BasePeriod {
    public Date start;
    public Date end;
    public List<Extension> extension;
    
    public static BasePeriod fromPeriod(Period period) {        
        if(period == null) return null;
        
        var entity = new BasePeriod();        
        entity.start = period.hasStart()? period.getStart() : null;
        entity.end = period.hasEnd()? period.getEnd() : null;
        entity.extension = period.hasExtension()? period.getExtension() : null;
        return entity;
    }
    
    public static List<BasePeriod> fromPeriodList(List<Period> periodList) {
        return transform(periodList, x -> fromPeriod(x));
    }
    
    public static Period toPeriod(BasePeriod entity) {
        if(entity == null) return null;
                
        var period = new Period();
        period.setStart(entity.start);
        period.setEnd(entity.end);
        period.setExtension(entity.extension);
        return period;
    }
    
    
    public static List<Period> toPeriodList(List<BasePeriod> entityList) {
        return transform(entityList, x -> toPeriod(x));
    }

}
