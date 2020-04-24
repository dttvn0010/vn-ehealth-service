package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneType;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "allergyIntolerance")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class AllergyIntoleranceEntity extends BaseResource {
    
    public static class AllergyIntoleranceReaction extends BaseBackboneType {
        public BaseCodeableConcept substance;
        public List<BaseCodeableConcept> manifestation;
        public String description;
        public Date onset;
        public String severity;
        public BaseCodeableConcept exposureRoute;
        public List<BaseAnnotation> note;
    }
    
	@Id
    @Indexed(name = "_id_")
	@JsonIgnore public ObjectId id;
	public List<BaseIdentifier> identifier;
	public BaseCodeableConcept clinicalStatus;
	public BaseCodeableConcept verificationStatus;
	public String type;
	public List<String> category;
	public String criticality;
	public BaseCodeableConcept code;
	public BaseReference patient;
	public BaseReference encounter;
	public BaseType onset;
	public Date recordedDate;
	public BaseReference recorder;
	public BaseReference asserter;
	public Date lastOccurrence;
	public List<BaseAnnotation> note;
	public List<AllergyIntoleranceReaction> reaction;
}
