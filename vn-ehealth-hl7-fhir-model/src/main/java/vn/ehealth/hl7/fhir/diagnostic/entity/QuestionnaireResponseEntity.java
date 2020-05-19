package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

@Document(collection = "questionnaireResponse")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class QuestionnaireResponseEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public BaseIdentifier identifier;
	public List<BaseReference> basedOn;
	public List<BaseReference> partOf;
	public String questionnaire;
	public String status;
	public BaseReference subject;
	public BaseReference encounter;
	public Date authored;
	public BaseReference author;
	public BaseReference source;
	public List<QuestionnaireResponseItem> item;

	public static class QuestionnaireResponseItem {
		public String linkId;
		public String definition;
		public String text;
		public List<QuestionnaireResponseItemAnswer> answer;
		public List<QuestionnaireResponseItem> item;

		public static class QuestionnaireResponseItemAnswer {
			public BaseType value;
			public List<QuestionnaireResponseItem> item;
		}
	}
}
