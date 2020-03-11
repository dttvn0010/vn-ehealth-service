package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.Optional;

import org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterLocationStatus;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;


import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class EncounterLocationEntity {
    public BaseReference location;
    public String status;
    public BasePeriod period;
    
    
    public static EncounterLocationEntity fromEncounterLocationComponent(EncounterLocationComponent obj) {
        if(obj == null) return null;
        var ent = new EncounterLocationEntity();
        ent.location = BaseReference.fromReference(obj.getLocation());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        return ent;
    }
    
    public static EncounterLocationComponent toEncounterLocationComponent(EncounterLocationEntity ent) {
        if(ent == null) return null;
        var obj = new EncounterLocationComponent();
        obj.setLocation(BaseReference.toReference(ent.location));
        obj.setStatus(EncounterLocationStatus.fromCode(ent.status));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        return obj;
        
    }
}
