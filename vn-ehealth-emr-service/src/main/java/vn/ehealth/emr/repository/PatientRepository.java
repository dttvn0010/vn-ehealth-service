package vn.ehealth.emr.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

public interface PatientRepository extends MongoRepository<PatientEntity, String> {

}
