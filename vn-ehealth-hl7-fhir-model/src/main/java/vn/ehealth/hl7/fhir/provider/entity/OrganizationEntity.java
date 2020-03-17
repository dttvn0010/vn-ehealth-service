package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "organization")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class OrganizationEntity extends BaseResource {
    @Id
    public ObjectId id;

    public List<BaseIdentifier> identifier;
    public List<BaseCodeableConcept> type;
    public String name;
    public List<String> alias;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public BaseReference partOf;
    public List<OrganizationContactEntity> contact;
    
    public static OrganizationEntity fromOrganization(Organization obj) {
        if(obj == null) return null;
        
        var ent = new OrganizationEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.name = obj.getName();
        ent.alias = transform(obj.getAlias(), x -> x.asStringValue());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.address = BaseAddress.fromAddressList(obj.getAddress());
        ent.partOf = BaseReference.fromReference(obj.getPartOf());
        ent.contact = transform(obj.getContact(), OrganizationContactEntity::fromOrganizationContactComponent);
        return ent;
    }
    
    public static Organization toOrganization(OrganizationEntity ent) {
        if(ent == null) return null;
        
        var obj = new Organization();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setName(ent.name);
        obj.setAlias(transform(ent.alias, x -> new StringType(x)));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setAddress(BaseAddress.toAddressList(ent.address));
        obj.setPartOf(BaseReference.toReference(ent.partOf));

        obj.setContact(transform(ent.contact, OrganizationContactEntity::toOrganizationContactComponent));
        return obj;
    }
}
