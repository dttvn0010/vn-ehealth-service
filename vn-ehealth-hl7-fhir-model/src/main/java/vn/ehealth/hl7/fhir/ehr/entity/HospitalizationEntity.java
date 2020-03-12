package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class HospitalizationEntity{
    public BaseIdentifier preAdmissionIdentifier;
    public BaseReference origin;
    public BaseCodeableConcept admitSource;
    public BaseCodeableConcept reAdmission;
    public List<BaseCodeableConcept> dietPreference;
    public List<BaseCodeableConcept> specialCourtesy;
    public List<BaseCodeableConcept> specialArrangement;
    public BaseReference destination;
    public BaseCodeableConcept dischargeDisposition;
    
    public static HospitalizationEntity fromEncounterHospitalizationComponent(EncounterHospitalizationComponent obj) {
        if(obj == null) return null;
        var ent = new HospitalizationEntity();
        ent.preAdmissionIdentifier = BaseIdentifier.fromIdentifier(obj.getPreAdmissionIdentifier());
        ent.origin = BaseReference.fromReference(obj.getOrigin());
        ent.admitSource = BaseCodeableConcept.fromCodeableConcept(obj.getAdmitSource());
        ent.reAdmission = BaseCodeableConcept.fromCodeableConcept(obj.getReAdmission());
        ent.dietPreference = BaseCodeableConcept.fromCodeableConcept(obj.getDietPreference());
        ent.specialCourtesy = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialCourtesy());
        ent.specialArrangement = BaseCodeableConcept.fromCodeableConcept(obj.getSpecialArrangement());
        ent.destination = BaseReference.fromReference(obj.getDestination());
        ent.dischargeDisposition = BaseCodeableConcept.fromCodeableConcept(obj.getDischargeDisposition());
        return ent;
    }
    
    public static EncounterHospitalizationComponent toEncounterHospitalizationComponent(HospitalizationEntity ent) {
        if(ent == null) return null;
        var obj = new EncounterHospitalizationComponent();
        obj.setPreAdmissionIdentifier(BaseIdentifier.toIdentifier(ent.preAdmissionIdentifier));
        obj.setOrigin(BaseReference.toReference(ent.origin));
        obj.setAdmitSource(BaseCodeableConcept.toCodeableConcept(ent.admitSource));
        obj.setReAdmission(BaseCodeableConcept.toCodeableConcept(ent.reAdmission));
        obj.setDietPreference(BaseCodeableConcept.toCodeableConcept(ent.dietPreference));
        obj.setSpecialCourtesy(BaseCodeableConcept.toCodeableConcept(ent.specialCourtesy));
        obj.setSpecialArrangement(BaseCodeableConcept.toCodeableConcept(ent.specialArrangement));
        obj.setDestination(BaseReference.toReference(ent.destination));
        obj.setDischargeDisposition(BaseCodeableConcept.toCodeableConcept(ent.dischargeDisposition));
        return obj;
    }
}
