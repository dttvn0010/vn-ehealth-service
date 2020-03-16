package vn.ehealth.hl7.fhir.provider.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "practitioner")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class PractitionerEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String gender;
    public Date birthDate;
    /** photo **/
    public List<QualificationEntity> qualification;
    public List<BaseCodeableConcept> communication;
    
    public static PractitionerEntity fromPractitioner(Practitioner obj) {
        if(obj == null) return null;
        var ent = new PractitionerEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.name = BaseHumanName.fromHumanNameList(obj.getName());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.address = BaseAddress.fromAddressList(obj.getAddress());
        ent.gender = Optional.ofNullable(obj.getGender()).map(x -> x.toCode()).orElse(null);
        ent.birthDate = obj.getBirthDate();
        ent.qualification = transform(obj.getQualification(), QualificationEntity::fromPractitionerQualificationComponent);
        ent.communication = BaseCodeableConcept.fromCodeableConcept(obj.getCommunication());
        return ent;
    }
    
    public static Practitioner toPractitioner(PractitionerEntity ent) {
        if(ent == null) return null;
        var obj = new Practitioner();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setName(BaseHumanName.toHumanNameList(ent.name));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setAddress(BaseAddress.toAddressList(ent.address));
        obj.setGender(AdministrativeGender.fromCode(ent.gender));
        obj.setBirthDate(ent.birthDate);
        obj.setQualification(transform(ent.qualification, QualificationEntity::toPractitionerQualificationComponent));
        obj.setCommunication(BaseCodeableConcept.toCodeableConcept(ent.communication));
        return obj;
    }
}
