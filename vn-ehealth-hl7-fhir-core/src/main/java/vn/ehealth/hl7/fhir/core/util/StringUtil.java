package vn.ehealth.hl7.fhir.core.util;

import java.util.UUID;

public class StringUtil {

    public static String generateUID() {
        //return  UUID.randomUUID().toString().replace("-", "");
    	return  UUID.randomUUID().toString();
    }
    
	public static String getType(String input) {
		if (input != null) {
			int pos = input.indexOf("/");
			if (pos > 0)
				return input.substring(0, pos);
		}
		return "";
	}

	public static String getId(String input) {
    	if(input != null) {
    		int pos = input.indexOf("/");
    		if(pos > 0) return input.substring(pos + 1);
    		else return input;
    	}
    	return "";
    }
}
