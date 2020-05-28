package vn.ehealth.hl7.fhir.definitionalartifact.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseContactDetail;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

@Document(collection = "questionnaire")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class QuestionnaireEntity extends BaseResource {

	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;

	public String url;
	public List<BaseIdentifier> identifier;
	public String version;
	public String name;
	public String title;
	public String derivedFrom;
	public String status;
	public boolean experimental;
	public List<String> subjectType;
	public Date date;
	public String publisher;
	public List<BaseContactDetail> contact;
	public String description;
	public List<BaseUsageContext> useContext;
	public List<BaseContactDetail> jurisdiction;
	public String purpose;
	public String copyright;
	public Date approvalDate;
	public Date lastReviewDate;
	public BasePeriod effectivePeriod;
	public List<BaseCoding> code;
	public List<QuestionnaireItem> item;

	public static class QuestionnaireItem {
		public String linkId;
		public String definition;
		public List<BaseCoding> code;
		public String prefix;
		public String text;
		public String type;
		public List<QuestionnaireItemEnableWhen> enableWhen;
		public String enableBehavior;
		public boolean required;
		public boolean repeats;
		public boolean readOnly;
		public int maxLength;
		public String answerValueSet;
		public List<QuestionnaireItemAnswerOption> answerOption;
		public List<QuestionnaireItemInitial> initial;
		public List<QuestionnaireItem> item;

		public static class QuestionnaireItemEnableWhen {
			public String question;
			public String operator;
			public BaseType answer;
		}

		public static class QuestionnaireItemAnswerOption {
			public BaseType value;
			public boolean initialSelected;
		}

		public static class QuestionnaireItemInitial {
			public BaseType value;
		}
	}

}
