package vn.ehealth.hl7.fhir.core.common;

public class UnAuthorizedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public UnAuthorizedException() {
        super();
    }
    
    public UnAuthorizedException(String message) {
        super(message);
    }

}
