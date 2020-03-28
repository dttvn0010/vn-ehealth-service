package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Extension;

public class BaseAttachment {
    public String contentType;
    public String language;
    public String data;
    public String url;
    public Integer size;
    public String hash;
    public String title;
    public Date creation;
    public List<Extension> extension;
}
