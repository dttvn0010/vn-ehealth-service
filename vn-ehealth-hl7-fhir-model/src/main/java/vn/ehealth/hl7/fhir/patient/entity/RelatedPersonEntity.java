package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "relatedPerson")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class RelatedPersonEntity extends BaseResource {
    
    public static class RelatedPersonCommunication {
        public BaseCodeableConcept language;
        public Boolean preferred;
    }
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public Boolean active;
    public List<BaseIdentifier> identifier;
    public BaseReference patient;
    public List<BaseCodeableConcept> relationship;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    public List<BaseAddress> address;
    public List<BaseAttachment> photo;
    public BasePeriod period;
    public List<RelatedPersonCommunication> communication;
}
