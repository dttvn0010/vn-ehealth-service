package vn.ehealth.hl7.fhir.core.util;

import java.util.UUID;

public class StringUtil {

    public static String generateUID() {
        //return  UUID.randomUUID().toString().replace("-", "");
    	return  UUID.randomUUID().toString();
    }
}
