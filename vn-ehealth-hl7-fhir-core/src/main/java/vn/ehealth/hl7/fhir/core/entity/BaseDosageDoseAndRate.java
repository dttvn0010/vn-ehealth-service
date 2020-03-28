package vn.ehealth.hl7.fhir.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Type;

public class BaseDosageDoseAndRate {

    public BaseCodeableConcept type;
    @JsonIgnore public Type dose;
    @JsonIgnore public Type rate;
    public List<Extension> extension;
}
