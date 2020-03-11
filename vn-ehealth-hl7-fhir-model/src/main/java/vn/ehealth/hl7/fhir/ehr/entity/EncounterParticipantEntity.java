package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent;

import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class EncounterParticipantEntity {
    public List<CodeableConcept> type;
    public BasePeriod period;
    public BaseReference individual;
    
    public static EncounterParticipantEntity fromEncounterParticipantComponent(EncounterParticipantComponent obj) {
        if(obj == null) return null;
        var ent = new EncounterParticipantEntity();
        ent.type = obj.getType();
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        ent.individual = BaseReference.fromReference(obj.getIndividual());
        return ent;
    }
    
    public static EncounterParticipantComponent toEncounterParticipantComponent(EncounterParticipantEntity ent) {
        if(ent == null) return null;
        var obj = new EncounterParticipantComponent();
        obj.setType(ent.type);
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setIndividual(BaseReference.toReference(ent.individual));
        return obj;
    }
}
