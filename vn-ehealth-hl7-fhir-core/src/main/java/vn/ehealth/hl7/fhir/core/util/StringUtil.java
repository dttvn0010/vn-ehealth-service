package vn.ehealth.hl7.fhir.core.util;

import java.util.UUID;

public class StringUtil {

    public static String generateUUID() {
        //return  UUID.randomUUID().toString().replace("-", "");
    	return  UUID.randomUUID().toString();
    }
    
    public static boolean isUUID(String st) {
    	try {
    		UUID.fromString(st);
    		return true;
    	}catch(IllegalArgumentException  e) {
    		
    	}
    	return false;
    }
}
