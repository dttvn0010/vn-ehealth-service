package vn.ehealth.hl7.fhir.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;
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
    
    private PatientAge getPatientAge(PatientEntity patient, Date toDate) {
        
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
   
    
    @GetMapping("/get_hsba")
    public ResponseEntity<?> getHsbaEncounter(@RequestParam Optional<String> patientId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Boolean> viewEntity,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
     
        var params =  mapOf("partOf.reference", (Object) null);
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        var criteria = MongoUtils.createCriteria(params);
        var lst = encounterDao.searchEntity(criteria, start.orElse(-1), count.orElse(-1));

        if(includePatient.orElse(false)) {
            lst.forEach(x -> MongoUtils.fetchReferenceResource(x.subject));
        }
        
        if(includeServiceProvider.orElse(false)) {
            lst.forEach(x -> MongoUtils.fetchReferenceResource(x.serviceProvider));
        }
        
        for(var enc : lst) {
            var patient = (PatientEntity) enc.subject.resource;
            if(patient != null && enc.period != null) {
                patient.computes.put("age", getPatientAge(patient, enc.period.end));
            }
        }
        
        if(viewEntity.orElse(false)) {
            return ResponseEntity.ok(lst);
        }else {
            var lstDto = transform(lst, EncounterEntity::toDto);            
            return ResponseEntity.ok(lstDto);
        }       
    }
}
