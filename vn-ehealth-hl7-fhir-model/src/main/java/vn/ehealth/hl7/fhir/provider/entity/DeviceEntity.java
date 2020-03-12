package vn.ehealth.hl7.fhir.provider.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Device.FHIRDeviceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "device")
public class DeviceEntity extends BaseResource {
    @Id
    public ObjectId id;

    public List<BaseIdentifier> identifier;
    public DeviceUdiEntity udi;
    public String status;
    public BaseCodeableConcept type;
    public String lotNumber;
    public String manufacturer;
    public Date manufactureDate;
    public Date expirationDate;
    public String modelNumber;
    public BaseReference patient;
    public BaseReference owner;
    public List<BaseContactPoint> contact;
    public BaseReference location;
    public String url;
    public List<BaseAnnotation> note;
    public List<BaseCodeableConcept> safety;
    
    public static Device toDevice(DeviceEntity ent) {
        if(ent == null) return null;
        var obj = new Device();
        obj.setStatus(FHIRDeviceStatus.fromCode(ent.status));
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setLotNumber(ent.lotNumber);        
        obj.setManufacturer(ent.manufacturer);
        obj.setManufactureDate(ent.manufactureDate);
        obj.setExpirationDate(ent.expirationDate);
        obj.setModelNumber(ent.modelNumber);
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setOwner(BaseReference.toReference(ent.owner));
        obj.setContact(BaseContactPoint.toContactPointList(ent.contact));
        obj.setLocation(BaseReference.toReference(ent.location));
        obj.setUrl(ent.url);
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setSafety(BaseCodeableConcept.toCodeableConcept(ent.safety));
        return obj;
    }
    
    public static DeviceEntity fromDevice(Device obj) {
        if(obj == null) return null;
        var ent = new DeviceEntity();
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.type = BaseCodeableConcept.fromCodeableConcept(obj.getType());
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.lotNumber = obj.getLotNumber();
        ent.manufacturer = obj.getManufacturer();
        ent.manufactureDate = obj.getManufactureDate();
        ent.expirationDate = obj.getExpirationDate();
        ent.modelNumber = obj.getModelNumber();
        ent.patient = BaseReference.fromReference(obj.getPatient());
        ent.owner = BaseReference.fromReference(obj.getOwner());
        ent.contact = BaseContactPoint.fromContactPointList(obj.getContact());
        ent.location = BaseReference.fromReference(obj.getLocation());
        ent.url = obj.getUrl();
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.safety = BaseCodeableConcept.fromCodeableConcept(obj.getSafety());
        return ent;
    }
}
