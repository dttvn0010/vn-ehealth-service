package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
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

@Document(collection = "immunization")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ImmunizationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    //public Boolean notGiven;
    public BaseCodeableConcept vaccineCode;
    public BaseReference patient;
    public BaseReference encounter;
    public Date recorded;
    public boolean primarySource;
    public BaseCodeableConcept reportOrigin;
    public BaseReference location;
    public BaseReference manufacturer;
    public String lotNumber;
    public Date expirationDate;
    public BaseCodeableConcept site;
    public BaseCodeableConcept route;
    public BaseQuantity doseQuantity;
    public List<ImmunizationPerformerEntity> performer;
    public List<BaseAnnotation> note;
    // public ImmunizationExplanationEntity explanation;
    public List<ImmunizationReactionEntity> reaction;
    // public List<ImmunizationVaccinationProtocolEntity> vaccinationProtocol;
    
}
