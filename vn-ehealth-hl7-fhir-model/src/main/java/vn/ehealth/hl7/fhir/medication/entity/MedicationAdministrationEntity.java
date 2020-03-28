package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "medicationAdministration")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationAdministrationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //    public List<BaseReference> definition;
    public List<BaseReference> partOf;
    public String status;
    public BaseCodeableConcept category;
    public Type medication;
    public BaseReference request;
    public BaseReference subject;
    public BaseReference context;
    public List<BaseReference> supportingInformation;
    @JsonIgnore public Type effective;
    /** performer **/
    //public Boolean notGiven;
    //public List<CodeableConcept> reasonNotGiven;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    //public BaseReference prescription;
    public List<BaseReference> device;
    public List<BaseAnnotation> note;
    /** dosage **/
    public List<BaseReference> eventHistory;
    
}
