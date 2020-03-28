package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;

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
}
