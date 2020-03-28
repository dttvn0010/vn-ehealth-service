package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "codeSystem")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class CodeSystemEntity extends BaseResource {
    
    public static class ContactDetail {
        public String name;
        public List<BaseContactPoint> telecom;
    }
    
    public static class Filter{

        public String code;
        public String description;
        public List<String> operator;
        public String value;
    }

    public static class CodeSystemProperty{

        public String code;
        public String uri;
        public String description;
        public String type;
    }
    
    @Id
    public ObjectId id;
    public String url;
    public List<BaseIdentifier> identifier;
    public String name;
    public String title;
    public String status;
    public boolean experimental;
    public Date date;
    public String publisher;
    public List<ContactDetail> contact;
    public String description;
    public List<BaseUsageContext> useContext;
    public List<BaseCodeableConcept> jurisdiction;
    public String purpose;
    public String copyright;
    public boolean caseSensitive;
    public String valueSet;
    public String hierarchyMeaning;
    public boolean compositional;
    public boolean versionNeeded;
    public String content;
    public String supplements;
    public Integer count;
    public List<Filter> filter;
    public List<CodeSystemProperty> property;
    public List<ConceptEntity> concept;
}
