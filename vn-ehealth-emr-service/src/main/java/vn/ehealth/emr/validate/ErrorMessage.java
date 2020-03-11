package vn.ehealth.emr.validate;

public class ErrorMessage {

    public class Code {
        final public static int INVALID_JSON = 1;
        final public static int MISSING_FIELD = 2;
        final public static int WRONG_DATA_TYPE = 3;
        final public static int NOT_ALLOW_FIELD = 4;
    }
    
    public String field;
    public int code;
    public String message;        
    
    public ErrorMessage(String field, int code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }
}
