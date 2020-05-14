package vn.ehealth.emr.controller.ehr;

import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.ehr.EncounterDTO;
import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.dao.util.DatabaseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/encounter")
public class EncounterController {

    @Autowired private EncounterDao encounterDao;
    
    @JsonInclude(Include.NON_NULL)
    public static class PatientAge {
        final public static int YEAR = 1;
        final public static int MONTH = 2;
        final public static int DAY = 3;
        
        public Integer value;
        public Integer unit;
        
        public PatientAge() {
        }
        
        public PatientAge(int tuoi, int donvi) {
            this.value = tuoi;
            this.unit = donvi;                
        }
    }
    
    private PatientAge getPatientAge(PatientDTO patient, Date toDate) {
        
        if(patient == null
            || patient.birthDate == null
            || toDate == null ) {
            return null;
        }
        
        var d1 = new java.sql.Date(patient.birthDate.getTime()).toLocalDate();
        var d2 = new java.sql.Date(toDate.getTime()).toLocalDate();
        
        var m1 = YearMonth.from(d1);
        var m2 = YearMonth.from(d2);
        
        var y1 = Year.from(d1);
        var y2 = Year.from(d2);
        
        int days = (int) d1.until(d2, ChronoUnit.DAYS);
        int months = (int) m1.until(m2, ChronoUnit.MONTHS);
        int years = (int) y1.until(y2, ChronoUnit.YEARS);
        
        if(d1.getDayOfMonth() > d2.getDayOfMonth()) 
            months -= 1;
        
        if(d1.getMonthValue() > d2.getMonthValue() 
                || (d1.getMonthValue() == d2.getMonthValue() && d1.getDayOfMonth() > d2.getDayOfMonth())) 
            years -= 1;
        
        if(months < 1) {
            return new PatientAge(days, PatientAge.DAY);
        }else if(months < 36) {
            return new PatientAge(months, PatientAge.MONTH);
        }else {            
            return new PatientAge(years, PatientAge.YEAR);
        }
    }
    
    private Criteria createCriteria(Optional<String> patientId) {
        var params =  mapOf("partOf.reference", (Object) null);
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        return MongoUtils.createCriteria(params);
    }
    
    @GetMapping("/count_hsba")
    public long count(@RequestParam Optional<String> patientId) {
        
        var criteria = createCriteria(patientId);
        return encounterDao.countResource(criteria);
    }
   
    
    @GetMapping("/list_hsba")
    public ResponseEntity<?> getHsbaEncounter(@RequestParam Optional<String> patientId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
        
        var criteria = createCriteria(patientId);
        var encList = encounterDao.searchResource(criteria, start.orElse(-1), count.orElse(-1));

        if(includePatient.orElse(false)) {
            encList.forEach(x -> DatabaseUtil.setReferenceResource(x.getSubject()));
        }
        
        if(includeServiceProvider.orElse(false)) {
            encList.forEach(x -> DatabaseUtil.setReferenceResource(x.getServiceProvider()));
        }
        
        var encDtoList = transform(encList, EncounterDTO::fromFhir);
        
        for(var encDto : encDtoList) {
            if(encDto.subject == null) continue;
            
            var patient = (Patient) encDto.subject.resource;
            var patientDto = PatientDTO.fromFhir(patient);
            encDto.computes.put("patient", patientDto);
            
            if(patient != null && encDto.period != null) {
            	patientDto.computes.put("age", getPatientAge(patientDto, encDto.period.end));
            }
        }
        
        return ResponseEntity.ok(encDtoList);
    }
    
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id, @RequestParam Optional<Boolean> includePatient) {
    	var enc = encounterDao.read(FhirUtil.createIdType(id));
    	
    	if(enc == null) {
            return new ResponseEntity<>("No encounter found", HttpStatus.NOT_FOUND);
        }
    	
    	if(includePatient.orElse(false)) {
    		DatabaseUtil.setReferenceResource(enc.getSubject());
    	}
    	
    	var encDto = EncounterDTO.fromFhir(enc);
    	if(encDto.subject != null) {
    		var patient = (Patient) encDto.subject.resource;
            var patientDto = PatientDTO.fromFhir(patient);
            encDto.computes.put("patient", patientDto);
            
            if(patient != null && encDto.period != null) {
            	patientDto.computes.put("age", getPatientAge(patientDto, encDto.period.end));
            }
    	}
    	
    	return ResponseEntity.ok(encDto);
    	
    }
}