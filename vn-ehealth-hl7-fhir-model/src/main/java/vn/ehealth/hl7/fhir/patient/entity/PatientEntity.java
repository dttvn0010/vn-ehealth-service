
package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.codesystems.ContactPointSystem;
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

import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
@Document(collection = "patient")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "idx_patient_by_default")
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
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    @JsonIgnore public Object deceased;
    public List<BaseAddress> address;
    public BaseCodeableConcept maritalStatus;
    @JsonIgnore public Object multipleBirth;
    public List<BaseAttachment> photo;    
    public List<Contact> contact;
    public List<PatientCommunication> communication;
    public List<BaseReference> generalPractitioner;    
    public BaseReference managingOrganization;
    public List<PatientLink> link;
    
    public List<BaseCodeableConcept> category;
    
    public String getAddress() {
        if(address.size() > 0) {
            return address.get(0).text;
        }
        return "";
    }
    
    public String getFullName() {
        if(name != null && name.size() > 0) {
            return name.get(0).text;
        }
        return "";
    }
    
    public String getBirthDate() {
        return DateUtil.parseDateToString(birthDate, DateUtil.DATE_FORMAT_D_M_Y);
    }
    
    public String getEmail() {
        var contact = FPUtil.findFirst(telecom, x -> ContactPointSystem.EMAIL.toCode().equals(x.system));
        if(contact != null) {
            return contact.value;
        }
        return "";
    }
    
    public String getPhone() {
        var contact = FPUtil.findFirst(telecom, x -> ContactPointSystem.PHONE.toCode().equals(x.system));
        if(contact != null) {
            return contact.value;
        }
        return "";
    }
    
    public String getNationalIdentifier() {
        var nationalIdentifier = FPUtil.findFirst(identifier, x -> IdentifierSystem.CMND.equals(x.system));
        if(nationalIdentifier != null) {
            return nationalIdentifier.value;
        }
        return  "";
    }
    
    public String getMohIdentifier() {
        var mohIdentifier = FPUtil.findFirst(identifier, x -> IdentifierSystem.THE_BHYT.equals(x.system));
        if(mohIdentifier != null) {
            return mohIdentifier.value;
        }
        return  "";
    }
    
    public BaseCodeableConcept getRace() {
        var extRace = FPUtil.findFirst(extension, x -> ExtensionURL.DAN_TOC.equals(x.url));
        if(extRace != null && extRace.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extRace.value;
        }
        return null;
    }
    
    public BaseCodeableConcept getEthnics() {
        var extEthnics = FPUtil.findFirst(extension, x -> ExtensionURL.TON_GIAO.equals(x.url));
        if(extEthnics != null && extEthnics.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extEthnics.value;
        }
        return null;
    }
    
    public BaseCodeableConcept getNationality() {
        var extNationality = FPUtil.findFirst(modifierExtension, x -> ExtensionURL.QUOC_TICH.equals(x.url));
        if(extNationality != null && extNationality.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extNationality.value;
        }
        return null;
    }
    
    public BaseCodeableConcept getJobType() {
        var extJobType = FPUtil.findFirst(extension, x -> ExtensionURL.NGHE_NGHIEP.equals(x.url));
        if(extJobType != null && extJobType.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extJobType.value;
        }
        return null;
    }
}
