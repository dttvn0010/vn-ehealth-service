package vn.ehealth.emr.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.beans.factory.annotation.Autowired;
import vn.ehealth.emr.repository.ResourceRepository;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;

abstract public class ResourceService<ENT extends BaseResource, FHIR extends DomainResource> {

    @Autowired public ResourceRepository<ENT> repository;
    
    public FHIR getById(String fhirId) {
        return repository.findByFhirIdAndActive(fhirId, true).map(x -> toFhirEx(x)).orElse(null);
    }
    
    
    public List<FHIR> getAll() {
        return DataConvertUtil.transform(repository.findAll(), x -> toFhirEx(x));
    }
    
    public FHIR save(@Nonnull FHIR obj) {
        var ent = fromFhirEx(obj);
        
        var oldEnt = repository.findByFhirIdAndActive(obj.getId(), true).orElse(null);
        if(oldEnt != null) {            
            oldEnt.active = false;
            oldEnt.resDeleted = new Date();
            repository.save(oldEnt);
            ent.version = oldEnt.version + 1;
        }
        ent.active = true;
        ent.resCreated = new Date();
        return toFhirEx(repository.save(ent));
    }
    
    public void delete(String id) {
        repository.deleteById(id);
    }
    
    private ENT fromFhirEx(FHIR obj) {
        var ent = fromFhir(obj);
        if(ent != null) {
            if(obj.hasId()) {
                ent.fhirId = obj.getId();
            }else {
                ent.fhirId = StringUtil.generateUID();
            }
            DataConvertUtil.setMetaExt(obj, ent);
        }
        return ent;
    }
    
    private FHIR toFhirEx(ENT ent) {
        var obj = toFhir(ent);
        if(obj != null) {
            obj.setMeta(DataConvertUtil.getMeta(ent, "Patient-v1.0"));
            DataConvertUtil.getMetaExt(ent, obj);
            obj.setId(ent.fhirId);
        }
        return obj;
    }
    
    abstract ENT fromFhir(FHIR obj);
    abstract FHIR toFhir(ENT ent);
}
