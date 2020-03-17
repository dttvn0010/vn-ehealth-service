package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.Optional;

import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Encounter.StatusHistoryComponent;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;


public class EncounterStatusHistoryEntity{
    public String status;
    public BasePeriod period;
    
    public static EncounterStatusHistoryEntity fromStatusHistoryComponent(StatusHistoryComponent obj) {
        if(obj == null) return null;
        
        var ent = new EncounterStatusHistoryEntity();
        
        ent.status = obj.hasStatus()? Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null; 
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        
        return ent;
    }
    
    
    public static StatusHistoryComponent toStatusHistoryComponent(EncounterStatusHistoryEntity ent) {
        if(ent == null) return null;
        
        var obj = new StatusHistoryComponent();
        
        obj.setStatus(EncounterStatus.fromCode(ent.status));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        
        return obj;
    }
}
