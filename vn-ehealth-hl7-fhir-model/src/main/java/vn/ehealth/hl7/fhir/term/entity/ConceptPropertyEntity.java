package vn.ehealth.hl7.fhir.term.entity;


import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class ConceptPropertyEntity extends BaseResource {
    public String code;
    public BaseType value;
}
