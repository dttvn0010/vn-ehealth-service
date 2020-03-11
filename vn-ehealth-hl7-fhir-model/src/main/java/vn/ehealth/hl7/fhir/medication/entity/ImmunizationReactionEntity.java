package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;

import org.hl7.fhir.r4.model.Immunization.ImmunizationReactionComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ImmunizationReactionEntity {

    public Date date;
    public BaseReference detail;
    public boolean reported;
    
    public static ImmunizationReactionEntity fromImmunizationReactionComponent(ImmunizationReactionComponent obj) {
        if(obj == null) return null;
        var ent = new ImmunizationReactionEntity();
        ent.date = obj.getDate();
        ent.detail = BaseReference.fromReference(obj.getDetail());
        ent.reported = obj.getReported();
        return ent;
    }
    
    public static ImmunizationReactionComponent toImmunizationReactionComponent(ImmunizationReactionEntity ent) {
        if(ent == null) return null;
        var obj = new ImmunizationReactionComponent();
        obj.setDate(ent.date);
        obj.setDetail(BaseReference.toReference(ent.detail));
        obj.setReported(ent.reported);
        return obj;        
    }
}
