package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "medicationAdministration")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationAdministrationEntity extends BaseResource {
    
    public static class MedicationAdministrationDosage {
        public String text;
        public BaseCodeableConcept site;
        public BaseCodeableConcept route;
        public BaseCodeableConcept method;
        public BaseQuantity dose;
        public BaseType rate;
    }

    public static class MedicationAdministrationPerformer {
        public BaseCodeableConcept function;
        public BaseReference actor;
    }
    
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<String> instantiates;
    public List<BaseReference> partOf;
    public String status;
    public List<BaseCodeableConcept> statusReason;
    public BaseCodeableConcept category;
    public BaseType medication;
    public BaseReference request;
    public BaseReference subject;
    public BaseReference context;
    public List<BaseReference> supportingInformation;
    public BaseType effective;
    public List<MedicationAdministrationPerformer> performer;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> device;
    public List<BaseAnnotation> note;
    public MedicationAdministrationDosage dosage;
    public List<BaseReference> eventHistory;
    
}
