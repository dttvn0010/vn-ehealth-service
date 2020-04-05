package vn.ehealth.hl7.fhir.careprovision.entity;

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
import vn.ehealth.hl7.fhir.core.entity.BaseTiming;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "nutritionOrder")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class NutritionOrderEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	public List<BaseIdentifier> identifier;
	public List<String> instantiatesCanonical;
	public List<String> instantiatesUri;
	public List<String> instantiates;
	public String status;
	public String intent;
	public BaseReference patient;
	public BaseReference encounter;
	public Date dateTime;
	public BaseReference orderer;
	public List<BaseReference> allergyIntolerance;
	public List<BaseCodeableConcept> foodPreferenceModifier;
	public List<BaseCodeableConcept> excludeFoodModifier;
	public NutritionOrderOralDiet oralDiet;
	public List<NutritionOrderSupplement> supplement;
	public NutrionOrderEnteralFormula enteralFormula;
	public List<BaseAnnotation> note;

	public static class NutritionOrderOralDiet {
		public List<BaseCodeableConcept> type;
		public List<BaseTiming> schedule;
		public List<OralDietNutrient> nutrient;
		public List<OralDietTexture> texture;
		public List<BaseCodeableConcept> fluidConsistencyType;
		public String instruction;

		public static class OralDietNutrient {
			public BaseCodeableConcept modifier;
			public BaseQuantity amount;
		}

		public static class OralDietTexture {
			public BaseCodeableConcept modifier;
			public BaseCodeableConcept foodType;
		}
	}

	public static class NutritionOrderSupplement {
		public BaseCodeableConcept type;
		public String productName;
		public List<BaseTiming> schedule;
		public BaseQuantity quantity;
		public String instruction;
	}

	public static class NutrionOrderEnteralFormula {
		public BaseCodeableConcept baseFormulaType;
		public String baseFormulaProductName;
		public BaseCodeableConcept additiveType;
		public String additiveProductName;
		public BaseQuantity caloricDensity;
		public BaseCodeableConcept routeofAdministration;
		public List<EnteralFormulaAdministration> administration;
		public BaseQuantity maxVolumeToDeliver;
		public String administrationInstruction;

		public static class EnteralFormulaAdministration {
			public BaseTiming schedule;
			public BaseQuantity quantity;
			public BaseType rate;
		}
	}
}
