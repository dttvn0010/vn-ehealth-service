package vn.ehealth.emr.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public interface ResourceRepository <ENT extends BaseResource> extends MongoRepository<ENT, String> {
    
    Optional<ENT> findByFhirIdAndActive(String fhirId, boolean active);
}
