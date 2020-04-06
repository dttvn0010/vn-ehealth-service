package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseDataRequirement extends BaseComplexType {
	public String type;
	public String profile;
	public BaseType subject;
	public String mustSupport;
	public List<BaseDataRequirementCodeFilter> codeFilter;
	public List<BaseDataRequirementDateFilter> dateFilter;
	public int limit;
	public List<BaseDataRequirementSort> sort;
	
	@JsonInclude(Include.NON_NULL)
	public static class BaseDataRequirementCodeFilter {
		public String path;
		public String searchParam;
		public String valueSet;
		public List<BaseCoding> code;
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class BaseDataRequirementDateFilter {
		public String path;
		public String searchParam;
		public BaseType value;
	}
	
	@JsonInclude(Include.NON_NULL)
	public static class BaseDataRequirementSort {
		public String path;
		public String direction;
	}
}
