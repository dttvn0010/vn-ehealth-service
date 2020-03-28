package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class MedicationDispenseSubstitutionEntity {
    public boolean wasSubstituted;
    public BaseCodeableConcept type;
    public List<BaseCodeableConcept> reason;
    public List<BaseReference> responsibleParty;
}
