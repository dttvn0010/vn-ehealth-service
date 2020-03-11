package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Practitioner.PractitionerQualificationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class QualificationEntity {

    public List<BaseIdentifier> identifier;
    public CodeableConcept code;
    public BasePeriod period;
    public BaseReference issuer;
    public String practitionerEntityID;
    
    public static QualificationEntity fromPractitionerQualificationComponent(PractitionerQualificationComponent obj) {
        if(obj == null) return null;
        var ent = new QualificationEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.code = obj.getCode();
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        ent.issuer = BaseReference.fromReference(obj.getIssuer());
        return ent;
    }
    
    public static PractitionerQualificationComponent toPractitionerQualificationComponent(QualificationEntity ent) {
        if(ent == null) return null;
        var obj = new PractitionerQualificationComponent();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setCode(ent.code);
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setIssuer(BaseReference.toReference(ent.issuer));
        return obj;
    }
}
