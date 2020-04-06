package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseTriggerDefinition  extends BaseComplexType {
	public String type;
	public String name;
	public BaseType timing;
	public List<BaseDataRequirement> data;
	public BaseExpression condition;
}
