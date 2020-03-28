package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import vn.ehealth.hl7.fhir.core.entity.BaseResource;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class FilterEntity extends BaseResource {

    public String code;
    public String description;
    public List<String> operator;
    public String value;
}
