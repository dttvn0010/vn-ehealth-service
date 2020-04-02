package vn.ehealth.hl7.fhir.provider.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "practitioner")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class PractitionerEntity extends BaseResource {
    
    public static class PractitionerQualification {
        public List<BaseIdentifier> identifier;
        public BaseCodeableConcept code;
        public BasePeriod period;
        public BaseReference issuer;
    }
    
    @Id
    public ObjectId id;
    public Boolean active;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public String gender;
    public Date birthDate;
    public List<BaseAttachment> photo;
    public List<PractitionerQualification> qualification;
    public List<BaseCodeableConcept> communication;
}
