package vn.ehealth.emr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.service.PatientService;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired private PatientService patientService;
    
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getPatientById(@RequestParam String id) {
        var patient = patientService.getById(id).get();
        return ResponseEntity.ok(patient);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(patientService.getAll());
    }
    
}
