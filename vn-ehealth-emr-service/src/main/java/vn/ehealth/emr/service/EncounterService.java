package vn.ehealth.emr.service;

import org.hl7.fhir.r4.model.Encounter;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;

@Service
public class EncounterService extends ResourceService<EncounterEntity, Encounter>{

    @Override
    EncounterEntity fromFhir(Encounter obj) {
        return EncounterEntity.fromEncounter(obj);
    }

    @Override
    Encounter toFhir(EncounterEntity ent) {
        return EncounterEntity.toEncounter(ent);
    }

}
