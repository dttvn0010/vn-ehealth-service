package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class HospitalizationEntity{
    public BaseIdentifier preAdmissionIdentifier;
    public BaseReference origin;
    public CodeableConcept admitSource;
    public CodeableConcept reAdmission;
    public List<CodeableConcept> dietPreference;
    public List<CodeableConcept> specialCourtesy;
    public List<CodeableConcept> specialArrangement;
    public BaseReference destination;
    public CodeableConcept dischargeDisposition;
    
    public static HospitalizationEntity fromEncounterHospitalizationComponent(EncounterHospitalizationComponent obj) {
        if(obj == null) return null;
        var ent = new HospitalizationEntity();
        ent.preAdmissionIdentifier = BaseIdentifier.fromIdentifier(obj.getPreAdmissionIdentifier());
        ent.origin = BaseReference.fromReference(obj.getOrigin());
        ent.admitSource = obj.getAdmitSource();
        ent.reAdmission = obj.getReAdmission();
        ent.dietPreference = obj.getDietPreference();
        ent.specialCourtesy = obj.getSpecialCourtesy();
        ent.specialArrangement = obj.getSpecialArrangement();
        ent.destination = BaseReference.fromReference(obj.getDestination());
        ent.dischargeDisposition = obj.getDischargeDisposition();
        return ent;
    }
    
    public static EncounterHospitalizationComponent toEncounterHospitalizationComponent(HospitalizationEntity ent) {
        if(ent == null) return null;
        var obj = new EncounterHospitalizationComponent();
        obj.setPreAdmissionIdentifier(BaseIdentifier.toIdentifier(ent.preAdmissionIdentifier));
        obj.setOrigin(BaseReference.toReference(ent.origin));
        obj.setAdmitSource(ent.admitSource);
        obj.setReAdmission(ent.reAdmission);
        obj.setDietPreference(ent.dietPreference);
        obj.setSpecialCourtesy(ent.specialCourtesy);
        obj.setSpecialArrangement(ent.specialArrangement);
        obj.setDestination(BaseReference.toReference(ent.destination));
        obj.setDischargeDisposition(ent.dischargeDisposition);
        return obj;
    }
}
