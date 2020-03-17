package vn.ehealth.emr.service;

import java.util.Optional;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.repository.PatientRepository;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Service
public class PatientService extends ResourceService<PatientEntity, Patient> {

    @Autowired
    private PatientRepository patientRepository;
    
    public Optional<PatientEntity> findByIdentifier(String identifier) {
        return patientRepository.findByIdentifierValueAndActive(identifier, true);
    }

    @Override
    PatientEntity fromFhir(Patient obj) {
        return PatientEntity.fromPatient(obj);
    }

    @Override
    Patient toFhir(PatientEntity ent) {
        return PatientEntity.toPatient(ent);
    }    
}
