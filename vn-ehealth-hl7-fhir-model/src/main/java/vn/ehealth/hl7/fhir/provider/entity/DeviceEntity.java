package vn.ehealth.hl7.fhir.provider.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
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
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
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
}
