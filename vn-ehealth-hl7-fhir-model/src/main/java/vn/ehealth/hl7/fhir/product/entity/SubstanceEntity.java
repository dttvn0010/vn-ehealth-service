package vn.ehealth.hl7.fhir.product.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseRatio;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "substance")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class SubstanceEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;	

	public List<BaseIdentifier> identifier;
	public String status;
	public List<BaseCodeableConcept> category;
	public BaseCodeableConcept code;
	public String description;
	public List<SubstanceInstance> instance;
	public List<SubstanceIngredient> ingredient;
	
	public static class SubstanceInstance {
		public BaseIdentifier identifier;
		public Date expiry;
		public BaseQuantity quantity;
	}
	
	public static class SubstanceIngredient {
		public BaseRatio quantity;
		public BaseType substance;
	}
}
