package vn.ehealth.hl7.fhir.provider.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "device")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class DeviceEntity extends BaseResource {
    
    public static class DeviceUdiCarrier {
        public String deviceIdentifier;
        public String name;
        public String jurisdiction;
        public String carrierHRF;
        public byte[] carrierAIDC;
        public String issuer;
        public String entryType;
    }
    
    public static class DeviceDeviceName {
        public String name;
        public String type;
    }
    
    public static class DeviceSpecialization {
        public BaseCodeableConcept systemType;
        public String version;
    }
    
    public static class DeviceProperty {
        public BaseCodeableConcept type;
        public List<BaseQuantity> valueQuantity;
        public List<BaseCodeableConcept> valueCode;
    }
    
    public static class DeviceVersion {
        public BaseCodeableConcept type;
        public BaseIdentifier component;
        public String value;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;

    public List<BaseIdentifier> identifier;
    public BaseReference definition;
    
    public List<DeviceUdiCarrier> udiCarrier;
    public String status;
    public String distinctIdentifier;
    public List<BaseCodeableConcept> statusReason;
    
    public String lotNumber;
    public String serialNumber;
    public List<DeviceDeviceName> deviceName;
    public String manufacturer;
    public Date manufactureDate;
    public Date expirationDate;
    public String modelNumber;
    public String partNumber;
    public BaseCodeableConcept type;
    public List<DeviceSpecialization> specialization;   
    public List<DeviceVersion> version;
    public BaseReference patient;
    public BaseReference owner;
    public List<BaseContactPoint> contact;
    public BaseReference location;
    public String url;
    public List<BaseAnnotation> note;
    public List<BaseCodeableConcept> safety;
    public List<DeviceProperty> property;
    public BaseReference parent;
}
