package vn.ehealth.emr.dto.controller;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.model.dto.BenhNhan;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;

import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/benh_nhan")
public class BenhNhanController {
    
    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
            
    @Autowired private PatientDao patientDao;
    
    @GetMapping("/get_by_id/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        var obj = patientDao.read(new IdType(id));
        var dto = BenhNhan.fromFhir(obj);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = patientDao.search(new HashMap<>());
        var result = transform(lst, x -> BenhNhan.fromFhir((Patient) x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody BenhNhan dto) {
        try {
            var obj = BenhNhan.toFhir(dto);
            if(obj.hasId()) {
                obj = patientDao.update(obj, obj.getIdElement());
            }else {
                obj = patientDao.create(obj);
            }
            dto = BenhNhan.fromFhir(obj);
            var result =  mapOf("success", true, "dto", dto);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @JsonInclude(Include.NON_NULL)
    public static class TuoiBenhNhan {
        final public static int YEAR = 1;
        final public static int MONTH = 2;
        final public static int DAY = 3;
        
        public Integer tuoi;
        public Integer donvi;
        
        public TuoiBenhNhan() {
        }
        
        public TuoiBenhNhan(int tuoi, int donvi) {
            this.tuoi = tuoi;
            this.donvi = donvi;                
        }
    }
    
    public TuoiBenhNhan getTuoi(BenhNhan benhNhan, Date toDate) {
         
        if(benhNhan == null
            || benhNhan.ngaySinh == null
            || toDate == null) {
            return new TuoiBenhNhan();
        }
        
        var d1 = new java.sql.Date(benhNhan.ngaySinh.getTime()).toLocalDate();
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
        
        if(d1.getMonthValue() > d2.getMonthValue() || (d1.getMonthValue() == d2.getMonthValue() && d1.getDayOfMonth() > d2.getDayOfMonth())) 
            years -= 1;
        
        if(months < 1) {
            return new TuoiBenhNhan(days, TuoiBenhNhan.DAY);
        }else if(months < 36) {
            return new TuoiBenhNhan(months, TuoiBenhNhan.MONTH);
        }else {            
            return new TuoiBenhNhan(years, TuoiBenhNhan.YEAR);
        }
    }
    
    @GetMapping("/get_tuoi")
    public ResponseEntity<?> getTuoiBenhNhan(@RequestParam String patientId, @RequestParam("toDate") String toDateStr) {
    	Date toDate = DateUtil.parseStringToDate(toDateStr, DateUtil.FORMAT_YYYY_MM_DD_HH_MM_SS);
    	var patient = patientDao.read(createIdType(patientId));
    	var result = getTuoi(BenhNhan.fromFhir(patient), toDate);
    	return ResponseEntity.ok(result);
    }
    
}
