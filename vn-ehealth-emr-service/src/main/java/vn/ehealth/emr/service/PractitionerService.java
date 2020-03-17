package vn.ehealth.emr.service;

import java.util.Optional;

import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.repository.PractitionerRepository;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;

@Service
public class PractitionerService extends ResourceService<PractitionerEntity, Practitioner> {

    @Autowired private PractitionerRepository practitionerRepository;
    
    public Optional<PractitionerEntity> getByIdentifier(String identifier) {
        return practitionerRepository.findByIdentifierValueAndActive(identifier, true);
    }

    @Override
    PractitionerEntity fromFhir(Practitioner obj) {
        return PractitionerEntity.fromPractitioner(obj);
    }

    @Override
    Practitioner toFhir(PractitionerEntity ent) {
        return PractitionerEntity.toPractitioner(ent);
    }
}
