package vn.ehealth.emr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.repository.PatientRepository;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;
    
    public Optional<PatientEntity> getById(String id) {
        return patientRepository.findById(id);
    }
    
    public List<PatientEntity> getAll(){
        return patientRepository.findAll();
    }
}
