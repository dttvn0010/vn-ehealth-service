
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
import vn.ehealth.hl7.fhir.core.entity.BaseType;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "patient")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class PatientEntity extends BaseResource {
    
    public static class PatientCommunication {
        public BaseCodeableConcept language;
        public Boolean preferred;
    }
    
    public static class PatientLink {
        public BaseReference other;
        public String type;
    }
    
    public static class Contact {
        protected List<BaseCodeableConcept> relationship;
        protected BaseHumanName name;
        protected List<BaseContactPoint> telecom;
        protected BaseAddress address;
        protected String gender;
        protected BaseReference organization;
        protected BasePeriod period;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public Boolean active;
    public List<BaseIdentifier> identifier;    
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;    
    public String gender;    
    public Date birthDate;
    public BaseType deceased;    
    public List<BaseAddress> address;    
    public BaseCodeableConcept maritalStatus;
    public BaseType multipleBirth;
    public List<BaseAttachment> photo;    
    public List<Contact> contact;
    public List<PatientCommunication> communication;
    public List<BaseReference> generalPractitioner;    
    public BaseReference managingOrganization;
    public List<PatientLink> link;
    
    public List<BaseCodeableConcept> category;
}
