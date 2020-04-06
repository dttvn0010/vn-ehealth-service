package vn.ehealth.hl7.fhir.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.ehealth.hl7.fhir.core.common.UnAuthorizedException;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class ResponseUtil {
    private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
    

    public static ResponseEntity<?> errorResponse(Exception e) {
        logger.error("Error in HttpRequest:", e);        
        var result = mapOf("success", false, "error", e.getMessage());
        
        var status = HttpStatus.BAD_REQUEST;
        if(e instanceof UnAuthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
        }
        
        return new ResponseEntity<>(result, status);
    }
}
