package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class EncounterParticipantEntity {
    public List<BaseCodeableConcept> type;
    public BasePeriod period;
    public BaseReference individual;
    
    public static EncounterParticipantEntity fromEncounterParticipantComponent(EncounterParticipantComponent obj) {
        if(obj == null) return null;
        var ent = new EncounterParticipantEntity();
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        ent.individual = obj.hasIndividual()? BaseReference.fromReference(obj.getIndividual()) : null;
        return ent;
    }
    
    public static EncounterParticipantComponent toEncounterParticipantComponent(EncounterParticipantEntity ent) {
        if(ent == null) return null;
        var obj = new EncounterParticipantComponent();
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setIndividual(BaseReference.toReference(ent.individual));
        return obj;
    }
}
