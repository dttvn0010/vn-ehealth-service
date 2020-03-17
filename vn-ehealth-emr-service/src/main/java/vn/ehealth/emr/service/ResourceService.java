package vn.ehealth.emr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import vn.ehealth.emr.repository.ResourceRepository;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class ResourceService<ENT extends BaseResource> {

    @Autowired public ResourceRepository<ENT> repository;
    
    public Optional<ENT> getById(String id) {
        return repository.findById(id);
    }
    
    public Optional<ENT> getByFhirId(String fhirId) {
        return repository.findByFhirIdAndActive(fhirId, true);
    }
    
    public List<ENT> getAll() {
        return repository.findAll();
    }
    
    public ENT save(ENT entity) {
        return repository.save(entity);
    }
    
    public void delete(String id) {
        repository.deleteById(id);
    }
}
