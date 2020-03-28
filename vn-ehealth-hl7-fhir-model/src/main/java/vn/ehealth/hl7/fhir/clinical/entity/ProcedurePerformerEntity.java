package vn.ehealth.hl7.fhir.clinical.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ProcedurePerformerEntity {

    public BaseCodeableConcept function;
    public BaseReference actor;
    public BaseReference onBehalfOf;
}
