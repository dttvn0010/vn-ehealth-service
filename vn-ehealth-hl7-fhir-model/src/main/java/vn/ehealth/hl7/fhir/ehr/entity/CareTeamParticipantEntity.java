package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.r4.model.CodeableConcept;



import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class CareTeamParticipantEntity {
    public List<CodeableConcept> role;
    public BaseReference member;
    public BaseReference onBehalfOf;
    public BasePeriod period;
    
    public static CareTeamParticipantEntity fromCareTeamParticipantComponent(CareTeamParticipantComponent obj) {
        if(obj == null) return null;
        
        var ent = new CareTeamParticipantEntity();
        
        ent.role = obj.getRole();
        ent.member = BaseReference.fromReference(obj.getMember());
        ent.onBehalfOf = BaseReference.fromReference(obj.getOnBehalfOf());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        
        return ent;        
    }
    
    public static CareTeamParticipantComponent toCareTeamParticipantComponent(CareTeamParticipantEntity ent) {
        if(ent == null) return null;
        
        var obj = new CareTeamParticipantComponent();
        
        obj.setRole(ent.role);
        obj.setMember(BaseReference.toReference(ent.member));
        obj.setOnBehalfOf(BaseReference.toReference(ent.onBehalfOf));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        
        return obj;
    }
}
