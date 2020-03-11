package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;

public class MedicationPackageEntity{
    public CodeableConcept container;
    public List<MedicationPackageContentEntity> content;
    public List<MedicationPackageBatchEntity> batch;
    
}
