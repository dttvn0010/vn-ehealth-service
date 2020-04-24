package vn.ehealth.emr.dto.patient;

import java.util.Date;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.Patient;

import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.emr.dto.AddressDTO;
import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.CodeableConceptDTO;
import vn.ehealth.emr.dto.HumanNameDTO;
import vn.ehealth.emr.dto.IdentifierDTO;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;

@JsonInclude(Include.NON_NULL)
public class PatientDTO extends BaseDTO {
    public HumanNameDTO name;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date birthDate;    
    
    public String mohId;
    public Date mohIdExpiredDate;
    public String nationalId;
    public AddressDTO address;
    public String gender;
    public CodeableConceptDTO race;
    public CodeableConceptDTO ethnics;
    public CodeableConceptDTO jobTitle;
    public CodeableConceptDTO nationality;
    public String email;
    public String phone;
    
    public static PatientDTO fromFhir(Patient obj) {
        if(obj == null) return null;
        
        var dto = new PatientDTO();
        dto.id = obj.getId();
        dto.name = HumanNameDTO.fromFhir(obj.getNameFirstRep());
        dto.birthDate = obj.getBirthDate();
        
        var nationalId = FPUtil.findFirst(obj.getIdentifier(), 
                x -> IdentifierSystem.CMND.equals(x.getSystem()));

        if(nationalId != null) {
            dto.nationalId = IdentifierDTO.fromFhir(nationalId).value;
        }
        
        var mohId = FPUtil.findFirst(obj.getIdentifier(), 
                        x -> IdentifierSystem.DINH_DANH_Y_TE.equals(x.getSystem()));
        
        if(mohId != null) {
            var mohIdDTO = IdentifierDTO.fromFhir(mohId); 
            dto.mohId = mohIdDTO.value;
            dto.mohIdExpiredDate = mohIdDTO.end;
        }
        
        dto.address = AddressDTO.fromFhir(obj.getAddressFirstRep());
        
        if(obj.hasGender()) {
            dto.gender = obj.getGender().toCode();
        }
        
        var ext = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.DAN_TOC);
        if(ext != null && ext.getValue() instanceof CodeableConcept) {
            dto.race = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
        }
        
        ext = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.TON_GIAO);
        if(ext != null && ext.getValue() instanceof CodeableConcept) {
            dto.ethnics = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
        }
        
        ext = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.NGHE_NGHIEP);
        if(ext != null && ext.getValue() instanceof CodeableConcept) {
            dto.jobTitle = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
        }
        
        ext = FhirUtil.findExtensionByURL(obj.getModifierExtension(), ExtensionURL.QUOC_TICH);
        if(ext != null && ext.getValue() instanceof CodeableConcept) {
            dto.nationality = CodeableConceptDTO.fromFhir((CodeableConcept) ext.getValue());
        }
        
        var phone = FPUtil.findFirst(obj.getTelecom(), 
                x -> ContactPointSystem.PHONE.equals(x.getSystem()));

        if(phone != null && phone.hasValue()) {
            dto.phone = phone.getValue();
        }
        
        var email = FPUtil.findFirst(obj.getTelecom(), 
                        x -> ContactPointSystem.EMAIL.equals(x.getSystem()));
        
        if(email != null && email.hasValue()) {
            dto.email = email.getValue();
        }
               
        return dto;
    }
}
