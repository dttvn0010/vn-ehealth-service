package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPerson;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "relatedPerson")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class RelatedPersonEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public BaseReference patient;
    public List<BaseCodeableConcept> relationship;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    public List<BaseAddress> address;
    public List<BaseAttachment> photo;
    public BasePeriod period;
    public BaseContactPerson contact;// RelatedPerson not field
    
    public static RelatedPersonEntity fromRelatedPerson(RelatedPerson obj) {
        if(obj == null) return null;
        var ent = new RelatedPersonEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.patient = BaseReference.fromReference(obj.getPatient());
        ent.relationship = BaseCodeableConcept.fromCodeableConcept(obj.getRelationship());
        ent.name = BaseHumanName.fromHumanNameList(obj.getName());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.gender = Optional.ofNullable(obj.getGender()).map(x -> x.toString()).orElse(null);
        ent.birthDate = obj.getBirthDate();
        ent.address = BaseAddress.fromAddressList(obj.getAddress());
        ent.photo = BaseAttachment.fromAttachmentList(obj.getPhoto());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        return ent;
    }
    
    public static RelatedPerson toRelatedPerson(RelatedPersonEntity ent) {
        if(ent == null) return null;
        var obj = new RelatedPerson();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setRelationship(BaseCodeableConcept.toCodeableConcept(ent.relationship));
        obj.setName(BaseHumanName.toHumanNameList(ent.name));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setGender(AdministrativeGender.fromCode(ent.gender));
        obj.setBirthDate(ent.birthDate);
        obj.setAddress(BaseAddress.toAddressList(ent.address));
        obj.setPhoto(BaseAttachment.toAttachmentList(ent.photo));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        return obj;
    }
}
