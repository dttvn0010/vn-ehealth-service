package vn.ehealth.emr.repository;

import java.util.Optional;

import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

public interface LocationRepository extends ResourceRepository<LocationEntity> {

    Optional<LocationEntity> findByIdentifierValueAndActive(String identifierValue, boolean active);
}
