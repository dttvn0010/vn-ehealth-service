package vn.ehealth.hl7.fhir.diagnostic.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;



public class ProcedureRequestRequesterEntity {
    public BaseReference agent;
    public BaseReference onBehalfOf;
}
