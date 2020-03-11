package vn.ehealth.emr.cda;

public class ValidatorError {
	
	public static class ErrorCode {
		public static final String MISSING_TEMPLATE_ID = "01";
		public static final String MISSING_CODE = "02";
	}

	public String code;
	public String message;	
	public String className;
	public String parentClassName;
	public String[] params;
	
	public ValidatorError(String code, String message, String className, String parentClassName) {
		this.code = code;
		this.message = message;
		this.className = className;
		this.parentClassName = parentClassName;
		this.params = new String[0];
	}
	
	public ValidatorError(String code, String message, String className, String parentClassName, String ... params) {
		this.code = code;
		this.message = message;
		this.className = className;
		this.parentClassName = parentClassName;
		this.params = params;
	}
}
