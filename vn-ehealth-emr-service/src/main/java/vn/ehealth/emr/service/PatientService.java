package vn.ehealth.emr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.repository.PatientRepository;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Service
public class PatientService extends ResourceService<PatientEntity> {

    @Autowired
    private PatientRepository patientRepository;
    
    public Optional<PatientEntity> findByIdentifier(String identifier) {
        return patientRepository.findByIdentifierValueAndActive(identifier, true);
    }    
}
