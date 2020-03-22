package vn.ehealth.hl7.fhir.term.providers;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.CodeSystem;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class CodeSystemForm {
    public static Map<String, String> listError(CodeSystem codeSystem){
        Map<String, String> lsError = new HashMap<>();
        if(!codeSystem.hasIdentifier()) {
            lsError.put("identifier", "Identifier is not null");
        }
        if(!codeSystem.hasVersion()) {
            lsError.put("version", "Version is not null");
        }
        if(!codeSystem.hasName()) {
            lsError.put("name", "Name is not null");
        }
        if(!codeSystem.hasUrl()) {
            lsError.put("url", "Url is not null");
        }
        if(!codeSystem.hasTitle()) {
            lsError.put("title", "Title is not null");
        }
        if(!codeSystem.hasStatus()) {
            lsError.put("status", "Status is not null");
        }
        if(!codeSystem.hasPublisher()) {
            lsError.put("publisher", "Publisher is not null");
        }
        if(!codeSystem.hasConcept()) {
            lsError.put("concept", "Concept is not null");
        }
        return lsError;
    }
}
