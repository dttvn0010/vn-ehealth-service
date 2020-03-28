package vn.ehealth.hl7.fhir.provider.entity;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "location")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class LocationEntity extends BaseResource{
    
    public static class LocationPosition {
        protected BigDecimal longitude;
        protected BigDecimal latitude;
        protected BigDecimal altitude;
    }
    
    public static class LocationHoursOfOperation  {
        protected List<String> daysOfWeek;
        protected Boolean allDay;
        protected String openingTime;
        protected String closingTime;
    }
    
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
    public LocationPosition position;
    public BaseReference managingOrganization;
    public BaseReference partOf;
    public List<LocationHoursOfOperation> hoursOfOperation;
    public String availabilityExceptions;
    public List<BaseReference> endpoint;
 }
