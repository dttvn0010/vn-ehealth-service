package vn.ehealth.emr.entity.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.service.PatientService;
import vn.ehealth.emr.service.ResourceService;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@RestController
@RequestMapping("/api/patient")
public class PatientController extends BaseController<PatientEntity> {

    @Autowired private PatientService patientService;

    @Override
    protected ResourceService<PatientEntity> getEntService() {
        return patientService;
    }
}
