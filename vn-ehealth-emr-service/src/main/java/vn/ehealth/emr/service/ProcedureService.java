package vn.ehealth.emr.service;

import org.hl7.fhir.r4.model.Procedure;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;

@Service
public class ProcedureService extends ResourceService<ProcedureEntity, Procedure>{

    @Override
    ProcedureEntity fromFhir(Procedure obj) {
        return ProcedureEntity.fromProcedure(obj);
    }

    @Override
    Procedure toFhir(ProcedureEntity ent) {
        return ProcedureEntity.toProcedure(ent);
    }

}
