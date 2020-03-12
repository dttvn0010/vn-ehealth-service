package vn.ehealth.hl7.fhir.term.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.UsageContext;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "valueSet")
public class ValueSetEntity extends BaseResource {

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
    public List<UsageContext> useContext;
    public List<BaseCodeableConcept> jurisdiction;
    public boolean immutable;
    public String purpose;
    public String copyright;
    //public boolean extensible;
    public ValueSetComposeEntity compose;
    public ValueSetExpansionEntity expansion;
    
    public static ValueSetEntity fromValueSet(ValueSet obj) {
        if(obj == null) return null;
        
        var ent = new ValueSetEntity();
        ent.url = obj.getUrl();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.name = obj.getName();
        ent.title = obj.getTitle();
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.experimental = obj.getExperimental();
        ent.date = obj.getDate();
        ent.publisher = obj.getPublisher();
        ent.contact = transform(obj.getContact(), ContactDetailEntity::fromContactDetail);
        ent.description = obj.getDescription();
        ent.useContext = obj.getUseContext();
        ent.jurisdiction = BaseCodeableConcept.fromCodeableConcept(obj.getJurisdiction());
        ent.immutable = obj.getImmutable();
        ent.purpose = obj.getPurpose();
        ent.copyright = obj.getCopyright();
        ent.compose = ValueSetComposeEntity.fromValueSetComposeComponent(obj.getCompose());
        ent.expansion = ValueSetExpansionEntity.fromValueSetExpansionComponent(obj.getExpansion());
        
        return ent;
    }
    
    public static ValueSet toValueSet(ValueSetEntity ent) {
        if(ent == null) return null;
        var obj = new ValueSet();
        obj.setUrl(ent.url);
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setName(ent.name);
        obj.setTitle(ent.title);
        obj.setStatus(PublicationStatus.fromCode(ent.status));
        obj.setExperimental(ent.experimental);
        obj.setDate(ent.date);
        obj.setPublisher(ent.publisher);
        obj.setContact(transform(ent.contact, ContactDetailEntity::toContactDetail));
        obj.setDescription(ent.description);
        obj.setUseContext(ent.useContext);
        obj.setJurisdiction(BaseCodeableConcept.toCodeableConcept(ent.jurisdiction));
        obj.setImmutable(ent.immutable);
        obj.setPurpose(ent.purpose);
        obj.setCopyright(ent.copyright);
        obj.setCompose(ValueSetComposeEntity.toValueSetComposeComponent(ent.compose));
        obj.setExpansion(ValueSetExpansionEntity.toValueSetExpansionComponent(ent.expansion));
        
        return obj;
    }
}
