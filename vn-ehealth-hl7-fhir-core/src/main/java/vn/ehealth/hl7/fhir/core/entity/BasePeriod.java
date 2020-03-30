package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BasePeriod  extends BaseSimpleType  {
    public Date start;
    public Date end;
}
