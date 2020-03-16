package vn.ehealth.hl7.fhir.user.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Person;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "person")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class PersonEntity extends BaseResource{
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    public List<BaseAddress> address;
    public BaseAttachment photo;
    public BaseReference managingOrganization;
    public List<PersonLinkEntity> link;
    
    public static PersonEntity fromPerson(Person obj) {
        if(obj == null) return null;
        var ent = new PersonEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.name = BaseHumanName.fromHumanNameList(obj.getName());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.gender = Optional.ofNullable(obj.getGender()).map(x -> x.toCode()).orElse(null);
        ent.birthDate = obj.getBirthDate();
        ent.address = BaseAddress.fromAddressList(obj.getAddress());
        ent.photo = BaseAttachment.fromAttachment(obj.getPhoto());
        ent.managingOrganization = BaseReference.fromReference(obj.getManagingOrganization());
        ent.link = transform(obj.getLink(), PersonLinkEntity::fromPersonLinkComponent);
        return ent;
    }
    
    public static Person toPerson(PersonEntity ent) {
        if(ent == null) return null;
        var obj = new Person();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setName(BaseHumanName.toHumanNameList(ent.name));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setGender(AdministrativeGender.fromCode(ent.gender));
        obj.setBirthDate(ent.birthDate);
        obj.setAddress(BaseAddress.toAddressList(ent.address));
        obj.setPhoto(BaseAttachment.toAttachment(ent.photo));
        obj.setManagingOrganization(BaseReference.toReference(ent.managingOrganization));
        obj.setLink(transform(ent.link, PersonLinkEntity::toPersonLinkComponent));
        return obj;
    }
}
