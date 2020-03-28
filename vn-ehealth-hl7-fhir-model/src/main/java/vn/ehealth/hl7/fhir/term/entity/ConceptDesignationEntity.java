package vn.ehealth.hl7.fhir.term.entity;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class ConceptDesignationEntity extends BaseResource {

    public String language;
    public BaseCoding use;
    public String value;
    
}
