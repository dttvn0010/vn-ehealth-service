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
                "gender", "birthDate", "race.text", "ethic.text");
                
        var identifier = FieldUtil.getListProjection(patient.identifier, "system", "value");
        
        var name = FieldUtil.getListProjection(patient.name, "text");
        m.put("name", name);
        
        var address = FieldUtil.getListProjection(patient.address, 
                "addressLine1", "addressLine2", "addressLine3", "district", "city", "country", "text");
        
        m.put("address", address);
        
        return ResponseEntity.ok(patient);
    }
    
}
