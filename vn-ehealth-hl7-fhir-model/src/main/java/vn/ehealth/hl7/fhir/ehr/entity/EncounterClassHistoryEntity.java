package vn.ehealth.hl7.fhir.ehr.entity;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter.ClassHistoryComponent;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;

public class EncounterClassHistoryEntity{
    public Coding class_;
    public BasePeriod period;
    
    public static EncounterClassHistoryEntity fromClassHistoryComponent(ClassHistoryComponent obj) {
        if(obj == null) return null;
        
        var ent = new EncounterClassHistoryEntity();
        
        ent.class_ = obj.hasClass_()? obj.getClass_() : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        
        return ent;
    }
    
    public static ClassHistoryComponent toClassHistoryComponent(EncounterClassHistoryEntity ent) {
        if(ent == null) return null;
        
        var obj = new ClassHistoryComponent();
        
        obj.setClass_(ent.class_);
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        
        return obj;
    }
    
    
}
