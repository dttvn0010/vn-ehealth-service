package vn.ehealth.emr.repository;

import java.util.Optional;

import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

public interface PatientRepository extends ResourceRepository<PatientEntity> {

    Optional<PatientEntity> findByIdentifierValueAndActive(String identifierValue, boolean active);
}
