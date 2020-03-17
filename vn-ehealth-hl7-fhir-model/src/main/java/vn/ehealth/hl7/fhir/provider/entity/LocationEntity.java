package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Location.LocationMode;
import org.hl7.fhir.r4.model.Location.LocationStatus;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseGeoLocation;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "location")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
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
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.status = obj.hasStatus()?  Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null) : null;
        ent.operationalStatus = obj.hasOperationalStatus()? BaseCoding.fromCoding(obj.getOperationalStatus()) : null;
        ent.name = obj.hasName()? obj.getName() : null;
        ent.alias = obj.hasAlias()? transform(obj.getAlias(), x -> x.asStringValue()) : null;
        ent.description = obj.hasDescription()? obj.getDescription() : null;
        ent.mode = obj.hasMode()? Optional.ofNullable(obj.getMode()).map(x -> x.toCode()).orElse(null) : null;
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.telecom = obj.hasTelecom()? BaseContactPoint.fromContactPointList(obj.getTelecom()) : null;
        ent.address = obj.hasAddress()? BaseAddress.fromAddress(obj.getAddress()) : null;
        ent.physicalType = obj.hasPhysicalType()? BaseCodeableConcept.fromCodeableConcept(obj.getPhysicalType()) : null;
        ent.position = obj.hasPosition()? BaseGeoLocation.fromLocationPositionComponent(obj.getPosition()) : null;
        ent.managingOrganization = obj.hasManagingOrganization()? BaseReference.fromReference(obj.getManagingOrganization()) : null;
        ent.partOf = obj.hasPartOf()? BaseReference.fromReference(obj.getPartOf()) : null;
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
    
    public String getIdentifier() {
        String value = "";
        if(identifier != null && identifier.size() > 0) {
            value = identifier.get(0).value;
        }
        return value != null? value : "";
    }
    
    public BaseCodeableConcept getTypeBySystem(@Nonnull String system) {
        if(type != null) {
            return type.stream()
                        .filter(x -> FPUtil.anyMatch(x.coding, y -> system.equals(y.system)))
                        .findFirst()
                        .orElse(null);
        }
        return null;
    }
}
