package vn.ehealth.emr.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.hl7.fhir.r4.model.Patient;

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
        return dto;
    }
}
