package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ImmunizationVaccinationProtocolEntity {

    public Integer doseSequence;
    public String description;
    public BaseReference authority;
    public String series;
    public Integer seriesDoses;
    public List<CodeableConcept> targetDisease;
    public CodeableConcept doseStatus;
    public CodeableConcept doseStatusReason;
}
