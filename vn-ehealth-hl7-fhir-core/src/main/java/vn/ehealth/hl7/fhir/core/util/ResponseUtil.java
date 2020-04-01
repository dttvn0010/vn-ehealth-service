package vn.ehealth.hl7.fhir.core.util;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    private static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
    

    public static ResponseEntity<?> errorResponse(Exception e) {
        logger.error("Error in HttpRequest:", e);
        var result = mapOf("success", false, "error", e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}
