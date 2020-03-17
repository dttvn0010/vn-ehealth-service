package vn.ehealth.emr.repository;

import java.util.Optional;

import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;

public interface PractitionerRepository extends ResourceRepository<PractitionerEntity> {
    
    Optional<PractitionerEntity> findByIdentifierValueAndActive(String identifierValue, boolean active);
}
