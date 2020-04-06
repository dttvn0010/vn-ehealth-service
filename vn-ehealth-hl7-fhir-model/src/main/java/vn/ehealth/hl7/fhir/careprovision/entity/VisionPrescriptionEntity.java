package vn.ehealth.hl7.fhir.careprovision.entity;

import java.math.BigDecimal;
import java.util.Date;
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
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "visionPrescription")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class VisionPrescriptionEntity  extends BaseResource{
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	
	public List<BaseIdentifier> identifier;
	public String status;
	public Date created;
	public BaseReference patient;
	public BaseReference encounter;
	public Date dateWritten;
	public BaseReference prescriber;
	public List<VisionPrescriptionLensSpecification> lensSpecification;
	
	public static class VisionPrescriptionLensSpecification {
		public BaseCodeableConcept product;
		public String eye;
		public BigDecimal sphere;
		public BigDecimal cylinder;
		public int axis;
		public List<VisionPrescriptionLensSpecificationPrism> prism;
		public BigDecimal add;
		public BigDecimal power;
		public BigDecimal backCurve;
		public BigDecimal diameter;
		public BaseQuantity duration;
		public String color;
		public String brand;
		public List<BaseAnnotation> note;
		
		public static class VisionPrescriptionLensSpecificationPrism {
			public BigDecimal amount;
			public String base;
		}
	}
}
