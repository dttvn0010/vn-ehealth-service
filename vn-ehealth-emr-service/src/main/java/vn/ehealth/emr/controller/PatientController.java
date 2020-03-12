package vn.ehealth.emr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.context.FhirContext;
import vn.ehealth.emr.service.PatientService;
import vn.ehealth.emr.utils.FieldUtil;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired private PatientService patientService;
    
    @Autowired private FhirContext fhirContext;
    
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getPatientById(@RequestParam String id) {
        var patient = patientService.getById(id).get();
        //var patientResouce = PatientEntity.toPatient(patient);
        //var jsonParser = fhirContext.newJsonParser();
        //var json = jsonParser.encodeResourceToString(patientResouce);
        
        var m = FieldUtil.getProjection(patient, 
                "gender", "birthDate", "address","race.text", "ethic.text");
        
        return ResponseEntity.ok(m);
    }
    
}
