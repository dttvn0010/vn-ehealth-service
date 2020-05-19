package vn.ehealth.hl7.fhir.conformance.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseBackboneType;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseComplexType;
import vn.ehealth.hl7.fhir.core.entity.BaseContactDetail;
import vn.ehealth.hl7.fhir.core.entity.BaseElementDefinition;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

@Document(collection = "structureDefinition")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class StructureDefinitionEntity extends BaseResource {
	@Id
	@Indexed(name = "_id_")
	@JsonIgnore
	public ObjectId id;
	public List<BaseIdentifier> identifier;

	public String url;
	public String version;
	public String name;
	public String title;
	public String status;
	public Boolean experimental;
	public Date date;
	public String publisher;
	public List<BaseContactDetail> contact;
	public String description;
	public List<BaseUsageContext> useContext;
	public List<BaseCodeableConcept> jurisdiction;
	public String purpose;
	public String copyright;
	public List<BaseCoding> keyword;
	public String fhirVersion;
	public List<StructureDefinitionMappingComponent> mapping;
	public String kind;
	public Boolean abstract_;
	public List<StructureDefinitionContextComponent> context;
	public List<String> contextInvariant;
	public String type;
	public String baseDefinition;
	public String derivation;
	public StructureDefinitionSnapshotComponent snapshot;
	public StructureDefinitionSnapshotComponent differential;

	public static class StructureDefinitionMappingComponent extends BaseBackboneType {
		public String identity;
		public String uri;
		public String name;
		public String comment;
	}

	public static class StructureDefinitionContextComponent extends BaseBackboneType {
		public String type;
		public String expression;
	}

	public static class StructureDefinitionSnapshotComponent extends BaseBackboneType {
		public List<BaseElementDefinition> element;
	}

	public static class StructureDefinitionDifferentialComponent extends BaseBackboneType {
		public List<BaseElementDefinition> element;
	}
}
