
package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient.LinkType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPerson;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "patient")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "idx_patient_by_default")
public class PatientEntity extends BaseResource {
    public static class PatientCommunicationEntity {
        public BaseCodeableConcept language;
        public Boolean preferred;
    }
    
    public static class PatientLinkEntity {
        public BaseReference other;
        public LinkType type;
    }
    
    @Id
    @Indexed(name = "_id_")
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    @JsonIgnore public Type deceased;
    public List<BaseAddress> address;
    public BaseCodeableConcept maritalStatus;
    @JsonIgnore public Type multipleBirth;
    public List<BaseAttachment> photo;    
    public List<BaseContactPerson> contact;
    public List<PatientCommunicationEntity> communication;
    public List<BaseReference> generalPractitioner;    
    public BaseReference managingOrganization;
    public List<PatientLinkEntity> link;
    
    public List<BaseCodeableConcept> category;
}
