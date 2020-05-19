package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseElementDefinition extends BaseComplexType {
	public String path;
	public String representation;
	public String sliceName;
	public Boolean sliceIsConstraining;
	public String label;
	public List<String> code;
	public List<BaseElementDefinitionSlicingComponent> slicing;
	public String _short;
	public String definition;
	public String comment;
	public String requirements;
	public List<String> alias;
	public Integer min;
	public String max;
	public BaseElementDefinitionTypeComponent type;
	public BaseType defaultValue;
	public String meaningWhenMissing;
	public String orderMeaning;
	public BaseType fixed;
	public BaseType pattern;
	public List<BaseElementDefinitionExampleComponent> example;
	public BaseType minValue;
	public BaseType maxValue;
	public Integer maxLength;
	public List<String> condition;
	public List<BaseElementDefinitionConstraintComponent> constraint;
	public Boolean mustSupport;
	public Boolean isModifier;
	public String isModifierReason;
	public Boolean isSummary;
	public BaseElementDefinitionBindingComponent binding;
	public List<BaseElementDefinitionMappingComponent> mapping;

	public static class BaseElementDefinitionSlicingComponent {
		public List<BaseElementDefinitionSlicingDiscriminatorComponent> discriminator;
		public String description;
		public Boolean ordered;
		public String rules;

		public static class BaseElementDefinitionSlicingDiscriminatorComponent {
			public String type;
			public String path;
		}
	}

	public static class BaseElementDefinitionTypeComponent {
		public String code;
		public List<String> profile;
		public List<String> targetProfile;
		public List<String> aggregation;
		public String versioning;
	}
	
	public static class BaseElementDefinitionExampleComponent {
		public String label;
		public BaseType value;
	}
	
	public static class BaseElementDefinitionConstraintComponent {
		public String key;
		public String requirements;
		public String severity;
		public String human;
		public String expression;
		public String xpath;
		public String source;
	}
	
	public static class BaseElementDefinitionBindingComponent {
		public String strength;
		public String description;
		public String valueSet;
	}
	
	public static class BaseElementDefinitionMappingComponent {
		public String identity;
		public String language;
		public String map;
		public String comment;
	}
}
