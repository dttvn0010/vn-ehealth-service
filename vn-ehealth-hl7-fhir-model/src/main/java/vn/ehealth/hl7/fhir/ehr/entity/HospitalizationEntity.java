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
        ent.preAdmissionIdentifier = obj.hasPreAdmissionIdentifier()? BaseIdentifier.fromIdentifier(obj.getPreAdmissionIdentifier()) : null;
        ent.origin = obj.hasOrigin()? BaseReference.fromReference(obj.getOrigin()): null;
        ent.admitSource = obj.hasAdmitSource()? BaseCodeableConcept.fromCodeableConcept(obj.getAdmitSource()): null;
        ent.reAdmission = obj.hasReAdmission()? BaseCodeableConcept.fromCodeableConcept(obj.getReAdmission()): null;
        ent.dietPreference = obj.hasDietPreference()? BaseCodeableConcept.fromCodeableConcept(obj.getDietPreference()): null;
        ent.specialCourtesy = obj.hasSpecialCourtesy()? BaseCodeableConcept.fromCodeableConcept(obj.getSpecialCourtesy()): null;
        ent.specialArrangement = obj.hasSpecialArrangement()? BaseCodeableConcept.fromCodeableConcept(obj.getSpecialArrangement()): null;
        ent.destination = obj.hasDestination()? BaseReference.fromReference(obj.getDestination()): null;
        ent.dischargeDisposition = obj.hasDischargeDisposition()? BaseCodeableConcept.fromCodeableConcept(obj.getDischargeDisposition()): null;
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
