package vn.ehealth.hl7.fhir.careprovision.entity;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "riskAssessment")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class RiskAssessmentEntity extends BaseResource{

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	
	public List<BaseIdentifier> identifier;
	public BaseReference basedOn;
	public BaseReference parent;
	public String status;
	public BaseCodeableConcept method;
	public BaseCodeableConcept code;
	public BaseReference subject;
	public BaseReference encounter;
	public BaseType occurrence;
	public BaseReference condition;
	public BaseReference performer;
	public List<BaseCodeableConcept> reasonCode;
	public List<BaseReference> reasonReference;
	public List<BaseReference> basis;
	public List<RiskAssessmentPrediction> prediction;
	public String mitigation;
	public List<BaseAnnotation> note;
	
	public static class RiskAssessmentPrediction {
		public BaseCodeableConcept outcome;
		public BaseType probability;
		public BaseCodeableConcept qualitativeRisk;
		public BigDecimal relativeRisk;
		public BaseType when;
		public String rationale;
	}
}
