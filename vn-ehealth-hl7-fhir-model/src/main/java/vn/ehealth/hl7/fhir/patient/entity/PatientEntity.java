
package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.codesystems.ContactPointSystem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

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
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.view.DTOView;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.utils.EntityUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

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
    @JsonIgnore public ObjectId id;
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
  
    // Computes method
  
    private String computeEmail() {
        var contact = FPUtil.findFirst(telecom, x -> ContactPointSystem.EMAIL.toCode().equals(x.system));
        if(contact != null) {
            return contact.value;
        }
        return "";
    }
    
    private String computePhone() {
        var contact = FPUtil.findFirst(telecom, x -> ContactPointSystem.PHONE.toCode().equals(x.system));
        if(contact != null) {
            return contact.value;
        }
        return "";
    }
    
    private String computeNationalIdentifier() {
        var nationalIdentifier = FPUtil.findFirst(identifier, x -> IdentifierSystem.CMND.equals(x.system));
        if(nationalIdentifier != null) {
            return nationalIdentifier.value;
        }
        return  "";
    }
    
    private String computeMohIdentifier() {
        var mohIdentifier = FPUtil.findFirst(identifier, x -> IdentifierSystem.DINH_DANH_Y_TE.equals(x.system));
        if(mohIdentifier != null) {
            return mohIdentifier.value;
        }
        return  "";
    }
    
    private BaseCodeableConcept computeRace() {
        var extRace = EntityUtils.findExtensionByURL(extension, ExtensionURL.DAN_TOC);
        if(extRace != null && extRace.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extRace.value;
        }
        return null;
    }
    
    private BaseCodeableConcept computeEthnics() {
        var extEthnics = EntityUtils.findExtensionByURL(extension, ExtensionURL.TON_GIAO);
        if(extEthnics != null && extEthnics.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extEthnics.value;
        }
        return null;
    }
    
    private BaseCodeableConcept computeNationality() {
        var extNationality = EntityUtils.findExtensionByURL(modifierExtension, ExtensionURL.QUOC_TICH);
        if(extNationality != null && extNationality.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extNationality.value;
        }
        return null;
    }
    
    private BaseCodeableConcept computeJobType() {
        var extJobType = EntityUtils.findExtensionByURL(extension, ExtensionURL.NGHE_NGHIEP);
        if(extJobType != null && extJobType.value instanceof BaseCodeableConcept) {
            return (BaseCodeableConcept) extJobType.value;
        }
        return null;
    }
    
    public static Map<String, Object> toDto(PatientEntity ent) {
        if(ent == null) return null;
        return ent.getDto();
    }
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        return mapOf(
                "name", BaseHumanName.toDto(getFirst(name)),
                "address", BaseAddress.toDto(getFirst(address)),
                "birthdate", birthDate,
                "email", computeEmail(),
                "phone", computePhone(),
                "nationalIdentifier", computeNationalIdentifier(),
                "mohIdentifier", computeMohIdentifier(),
                "race", BaseCodeableConcept.toDto(computeRace()),
                "ethnics", BaseCodeableConcept.toDto(computeEthnics()),
                "nationality", BaseCodeableConcept.toDto(computeNationality()),
                "jobtype", BaseCodeableConcept.toDto(computeJobType())                    
            );
    }
}
