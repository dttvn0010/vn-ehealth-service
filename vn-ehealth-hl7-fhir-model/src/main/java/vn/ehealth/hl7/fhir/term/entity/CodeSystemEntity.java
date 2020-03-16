package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseUsageContext;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "codeSystem")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class CodeSystemEntity extends BaseResource {
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
    public List<ContactDetailEntity> contact;
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
    public Integer count;
    public List<FilterEntity> filter;
    public List<CodeSystemPropertyEntity> property;
    public List<ConceptEntity> concept;
    
    public static CodeSystem toCodeSystem(CodeSystemEntity ent) {
        if(ent == null) return null;
        
        var obj = new CodeSystem();
        obj.setUrl(ent.url);
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setName(ent.name);
        obj.setTitle(ent.title);
        obj.setExperimental(ent.experimental);
        obj.setDate(ent.date);
        obj.setPublisher(ent.publisher);
        obj.setContact(transform(ent.contact, ContactDetailEntity::toContactDetail));
        obj.setDescription(ent.description);
        obj.setUseContext(transform(ent.useContext, BaseUsageContext::toUsageContext));
        obj.setJurisdiction(BaseCodeableConcept.toCodeableConcept(ent.jurisdiction));
        obj.setPurpose(ent.purpose);
        obj.setCopyright(ent.copyright);
        obj.setCaseSensitive(ent.caseSensitive);
        obj.setValueSet(ent.valueSet);
        obj.setHierarchyMeaning(CodeSystemHierarchyMeaning.fromCode(ent.hierarchyMeaning));
        obj.setCompositional(ent.compositional);
        obj.setVersionNeeded(ent.versionNeeded);
        obj.setContent(CodeSystemContentMode.fromCode(ent.content));
        obj.setCount(ent.count);
        obj.setFilter(transform(ent.filter, FilterEntity::toCodeSystemFilterComponent));
        obj.setProperty(transform(ent.property, CodeSystemPropertyEntity::toPropertyComponent));
        obj.setConcept(transform(ent.concept, ConceptEntity::toConceptDefinitionComponent));
        return obj;
    }
    
    public static CodeSystemEntity fromCodeSystem(CodeSystem obj) {
        if(obj == null) return null;
        var ent = new CodeSystemEntity();
        ent.url = obj.getUrl();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.name = obj.getName();
        ent.title = obj.getTitle();
        ent.experimental = obj.getExperimental();
        ent.date = obj.getDate();
        ent.publisher = obj.getPublisher();
        ent.contact= transform(obj.getContact(), ContactDetailEntity::fromContactDetail);
        ent.description = obj.getDescription();
        ent.useContext = transform(obj.getUseContext(), BaseUsageContext::fromUsageContext);
        ent.jurisdiction = BaseCodeableConcept.fromCodeableConcept(obj.getJurisdiction());
        ent.purpose = obj.getPurpose();
        ent.copyright = obj.getCopyright();
        ent.caseSensitive = obj.getCaseSensitive();
        ent.valueSet = obj.getValueSet();
        ent.hierarchyMeaning = Optional.ofNullable(obj.getHierarchyMeaning()).map(x -> x.toCode()).orElse(null);
        ent.compositional = obj.getCompositional();
        ent.versionNeeded = obj.getVersionNeeded();
        ent.content = Optional.ofNullable(obj.getContent()).map(x -> x.toCode()).orElse(null);
        ent.count = obj.getCount();
        ent.filter = transform(obj.getFilter(), FilterEntity::fromCodeSystemFilterComponent);
        ent.property = transform(obj.getProperty(), CodeSystemPropertyEntity::fromPropertyComponent);
        ent.concept = transform(obj.getConcept(), ConceptEntity::fromConceptDefinitionComponent);        
        
        return ent;
    }
}
