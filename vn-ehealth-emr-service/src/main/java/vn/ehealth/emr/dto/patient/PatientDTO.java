package vn.ehealth.emr.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Patient;

import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@JsonInclude(Include.NON_NULL)
public class PatientDTO extends PatientEntity {   
    
	private Integer computeAge() {
		
		if(birthDate == null) return null;
        
        var now = new Date();
        
        var d1 = new java.sql.Date(birthDate.getTime()).toLocalDate();
        var d2 = new java.sql.Date(now.getTime()).toLocalDate();
        
        var y1 = Year.from(d1);
        var y2 = Year.from(d2);
        
        int years = (int) y1.until(y2, ChronoUnit.YEARS);
        
        
        if(d1.getMonthValue() > d2.getMonthValue() 
                || (d1.getMonthValue() == d2.getMonthValue() && d1.getDayOfMonth() > d2.getDayOfMonth())) 
            years -= 1;
        
        return years;
    }

    public static PatientDTO fromFhir(Patient obj) {
    	if(obj == null) return null;
    	
        var dto = fhirToEntity(obj, PatientDTO.class);
        dto.computes.put("age", dto.computeAge());
        
        var insuranceNumber = FhirUtil.findIdentifierBySystem(obj.getIdentifier(), IdentifierSystem.INSURANCE_NUMBER);
        dto.computes.put("insuranceNumber", insuranceNumber != null? insuranceNumber.getValue() : "");
        
        var nationalId = FhirUtil.findIdentifierBySystem(obj.getIdentifier(), IdentifierSystem.NATIONAL_ID);
        dto.computes.put("nationalId", nationalId != null? nationalId.getValue(): "");
        
        var phone = FhirUtil.findContactPointBySytem(obj.getTelecom(), ContactPointSystem.PHONE);
        dto.computes.put("phone", phone != null? phone.getValue() : "");
        
        var email = FhirUtil.findContactPointBySytem(obj.getTelecom(), ContactPointSystem.EMAIL);
        dto.computes.put("email", email != null? email.getValue() : "");        
        
        var raceExt = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.DAN_TOC);
        if(raceExt != null && raceExt.getValue() instanceof CodeableConcept) {
            dto.computes.put("race", CodingDTO.fromCodeableConcept((CodeableConcept) raceExt.getValue()));
        }
        
        var jobExt = FhirUtil.findExtensionByURL(obj.getExtension(), ExtensionURL.NGHE_NGHIEP);
        if(jobExt != null && jobExt.getValue() instanceof CodeableConcept) {
            dto.computes.put("job", CodingDTO.fromCodeableConcept((CodeableConcept) jobExt.getValue()));
        }
        
        var nationalityExt = FhirUtil.findExtensionByURL(obj.getModifierExtension(), ExtensionURL.QUOC_TICH);
        if(nationalityExt != null && nationalityExt.getValue() instanceof CodeableConcept) {
            dto.computes.put("nationality", CodingDTO.fromCodeableConcept((CodeableConcept) nationalityExt.getValue()));
        }
        
        return dto;
    }
}
