package vn.ehealth.hl7.fhir.term.providers;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.ValueSet;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class ValueSetForm {
    public static Map<String, String> listError(ValueSet valueSet){
        Map<String, String> lsError = new HashMap<>();
        if(!valueSet.hasIdentifier()) {
            lsError.put("identifier", "Identifier is not null");
        }
        if(!valueSet.hasVersion()) {
            lsError.put("version", "Version is not null");
        }
        if(!valueSet.hasName()) {
            lsError.put("name", "Name is not null");
        }
        if(!valueSet.hasUrl()) {
            lsError.put("url", "Url is not null");
        }
        if(!valueSet.hasTitle()) {
            lsError.put("title", "Title is not null");
        }
        if(!valueSet.hasStatus()) {
            lsError.put("status", "Status is not null");
        }
        if(!valueSet.hasPublisher()) {
            lsError.put("publisher", "Publisher is not null");
        }
        return lsError;
    }
}
