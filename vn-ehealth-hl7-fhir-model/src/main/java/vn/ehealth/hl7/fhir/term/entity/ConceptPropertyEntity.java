package vn.ehealth.hl7.fhir.term.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class ConceptPropertyEntity extends BaseResource {
    public String code;
    @JsonIgnore public Object value;
}
