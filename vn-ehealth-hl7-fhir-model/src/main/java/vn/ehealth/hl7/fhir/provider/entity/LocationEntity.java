package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationMode;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseGeoLocation;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "location")
public class LocationEntity extends BaseResource{
    @Id
    public ObjectId id;
    
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCoding operationalStatus;
    public String name;
    public List<String> alias;
    public String description;
    public String mode;
    public List<BaseCodeableConcept> type;
    public List<BaseContactPoint> telecom;
    public BaseAddress address;
    public BaseCodeableConcept physicalType;
    public BaseGeoLocation position;
    public BaseReference managingOrganization;
    public BaseReference partOf;
    
    public static LocationEntity fromLocation(Location obj) {
        if(obj == null) return null;
        
        var ent = new LocationEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status =  Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.operationalStatus = BaseCoding.fromCoding(obj.getOperationalStatus());
        ent.name =  obj.getName();
        ent.alias = transform(obj.getAlias(), x -> x.asStringValue());
        ent.description = obj.getDescription();
        ent.mode = Optional.ofNullable(obj.getMode()).map(x -> x.toCode()).orElse(null);
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.address = BaseAddress.fromAddress(obj.getAddress());
        ent.physicalType = BaseCodeableConcept.fromCodeableConcept(obj.getPhysicalType());
        ent.position = BaseGeoLocation.fromLocationPositionComponent(obj.getPosition());
        ent.managingOrganization = BaseReference.fromReference(obj.getManagingOrganization());
        ent.partOf = BaseReference.fromReference(obj.getPartOf());
        return ent;
        
    }
    
    public static Location toLocation(LocationEntity ent) {
        if(ent == null) return null;
        var obj = new Location();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(LocationStatus.fromCode(ent.status));
        obj.setOperationalStatus(BaseCoding.toCoding(ent.operationalStatus));
        obj.setName(ent.name);
        obj.setAlias(transform(ent.alias, x -> new StringType(x)));
        obj.setDescription(ent.description);
        obj.setMode(LocationMode.fromCode(ent.mode));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setAddress(BaseAddress.toAddress(ent.address));
        obj.setPhysicalType(BaseCodeableConcept.toCodeableConcept(ent.physicalType));
        obj.setPosition(BaseGeoLocation.toLocationPositionComponent(ent.position));
        obj.setManagingOrganization(BaseReference.toReference(ent.managingOrganization));
        obj.setPartOf(BaseReference.toReference(ent.partOf));
        return obj;
        
    }
}
