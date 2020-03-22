package vn.ehealth.hl7.fhir.term.providers;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.ConceptMap;
/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class ConceptMapForm {
    public static Map<String, String> listError(ConceptMap conceptMap){
        Map<String, String> lsError = new HashMap<>();
        if(!conceptMap.hasIdentifier()) {
            lsError.put("identifier", "Identifier is not null");
        }
        if(!conceptMap.hasVersion()) {
            lsError.put("version", "Version is not null");
        }
        if(!conceptMap.hasName()) {
            lsError.put("name", "Name is not null");
        }
        if(!conceptMap.hasUrl()) {
            lsError.put("url", "Url is not null");
        }
        if(!conceptMap.hasTitle()) {
            lsError.put("title", "Title is not null");
        }
        if(!conceptMap.hasStatus()) {
            lsError.put("status", "Status is not null");
        }
        if(!conceptMap.hasPublisher()) {
            lsError.put("publisher", "Publisher is not null");
        }
        if(!conceptMap.hasSource()) {
            lsError.put("source", "Source is not null");
        }
        if(!conceptMap.hasTarget()) {
            lsError.put("target", "Target is not null");
        }
        if(!conceptMap.hasGroup()) {
            lsError.put("group", "Group is not null");
        }else {
            //continue validate
        }
        return lsError;
    }
}
