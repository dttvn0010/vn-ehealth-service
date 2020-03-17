package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class QualificationEntity {

    public List<BaseIdentifier> identifier;
    public BaseCodeableConcept code;
    public BasePeriod period;
    public BaseReference issuer;
    
    public static QualificationEntity fromPractitionerQualificationComponent(PractitionerQualificationComponent obj) {
        if(obj == null) return null;
        var ent = new QualificationEntity();
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        ent.issuer = obj.hasIssuer()? BaseReference.fromReference(obj.getIssuer()) : null;
        return ent;
    }
    
    public static PractitionerQualificationComponent toPractitionerQualificationComponent(QualificationEntity ent) {
        if(ent == null) return null;
        var obj = new PractitionerQualificationComponent();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setIssuer(BaseReference.toReference(ent.issuer));
        return obj;
    }
}
