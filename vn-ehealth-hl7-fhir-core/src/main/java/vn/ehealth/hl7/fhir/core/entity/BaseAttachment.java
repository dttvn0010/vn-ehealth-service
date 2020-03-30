package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseAttachment extends BaseComplexType {
    public String contentType;
    public String language;
    public byte[] data;
    public String url;
    public Integer size;
    public byte[] hash;
    public String title;
    public Date creation;
}
