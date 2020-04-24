package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "immunization")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ImmunizationEntity extends BaseResource {
    
    public static class ImmunizationPerformer extends BaseBackboneElement {

        public BaseCodeableConcept function;
        public BaseReference actor;
        
    }
    
    public static class ImmunizationReaction extends BaseBackboneElement {

        public Date date;
        public BaseReference detail;
        public boolean reported;
        
    }
    public static class ImmunizationEducation extends BaseBackboneElement {
        public String documentType;
        public String reference;
        public Date publicationDate;
        public Date presentationDate;
    }
    
    public static class ImmunizationProtocolApplied extends BaseBackboneElement {
        public String series;
        public BaseReference authority;
        public List<BaseCodeableConcept> targetDisease;
        @JsonIgnore protected BaseType doseNumber;
        @JsonIgnore protected BaseType seriesDoses;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept statusReason;
    public BaseCodeableConcept vaccineCode;
    public BaseReference patient;
    public BaseReference encounter;
    public BaseType occurrence;
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
    public List<ImmunizationPerformer> performer;
    public List<BaseAnnotation> note;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public Boolean isSubpotent;
    public List<BaseCodeableConcept>  subpotentReason;
    public List<ImmunizationEducation> education;
    public List<BaseCodeableConcept> programEligibility;
    public BaseCodeableConcept fundingSource;
    public List<ImmunizationReaction> reaction;
    public List<ImmunizationProtocolApplied> protocolApplied;
    
}
