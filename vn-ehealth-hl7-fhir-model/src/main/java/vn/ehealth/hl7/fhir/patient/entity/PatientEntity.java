
package vn.ehealth.hl7.fhir.patient.entity;

import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.Patient;
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
@Document(collection = "idxPatient")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "idx_patient_by_default")
public class PatientEntity extends BaseResource {
    @Id
    @Indexed(name = "_id_")
    public String id;
    public List<BaseIdentifier> identifier;
    public List<BaseHumanName> name;
    public List<BaseContactPoint> telecom;
    public String gender;
    public Date birthDate;
    
    @JsonIgnore
    public Type deceased;
    
    public List<BaseAddress> address;
    public BaseCodeableConcept maritalStatus;
    
    @JsonIgnore
    public Type multipleBirth;
    
    public List<BaseAttachment> photo;    
    public List<BaseContactPerson> contact;
    public List<BaseReference> generalPractitioner;    
    public BaseReference managingOrganization;
    public BaseCodeableConcept ethic;
    public BaseCodeableConcept race;
    
    public static PatientEntity fromPatient(Patient obj) {  
        if(obj == null) return null;
        var ent = new PatientEntity();
        
        if(obj.hasIdentifier())
            ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        
        if(obj.hasName())
            ent.name = BaseHumanName.fromHumanNameList(obj.getName());
        
        if(obj.hasGender())
            ent.gender = obj.getGender().toCode();            
        
        if(obj.hasDeceased())
            ent.deceased = obj.getDeceased();
        
        if(obj.hasAddress())
            ent.address = BaseAddress.fromAddressList(obj.getAddress());
        
        if(obj.hasMaritalStatus())
            ent.maritalStatus = BaseCodeableConcept.fromCodeableConcept(obj.getMaritalStatus());
        
        if(obj.hasMultipleBirth())
            ent.multipleBirth = obj.getMultipleBirth();
        
        if(obj.hasPhoto())
            ent.photo = BaseAttachment.fromAttachmentList(obj.getPhoto());
        
        if(obj.hasContact())
            ent.contact = BaseContactPerson.fromContactComponentList(obj.getContact());
        
        if(obj.hasGeneralPractitioner())
            ent.generalPractitioner = BaseReference.fromReferenceList(obj.getGeneralPractitioner());
        
        if(obj.hasManagingOrganization())
            ent.managingOrganization = BaseReference.fromReference(obj.getManagingOrganization());

        return ent;
    }
    
    public static Patient toPatient(PatientEntity ent) {
        if(ent == null) return null;
        var obj = new Patient();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setName(BaseHumanName.toHumanNameList(ent.name));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setGender(AdministrativeGender.fromCode(ent.gender));
        obj.setDeceased(ent.deceased);
        obj.setAddress(BaseAddress.toAddressList(ent.address));
        obj.setMaritalStatus(BaseCodeableConcept.toCodeableConcept(ent.maritalStatus));
        obj.setMultipleBirth(ent.multipleBirth);
        obj.setPhoto(BaseAttachment.toAttachmentList(ent.photo));
        obj.setContact(BaseContactPerson.toContactComponentList(ent.contact));
        obj.setGeneralPractitioner(BaseReference.toReferenceList(ent.generalPractitioner));
        obj.setManagingOrganization(BaseReference.toReference(ent.managingOrganization));
        return obj;
    }
    
    public BaseHumanName getName() {
        if(name != null && name.size() > 0) {
            return name.get(0);
        }
        return null;
    }
}
