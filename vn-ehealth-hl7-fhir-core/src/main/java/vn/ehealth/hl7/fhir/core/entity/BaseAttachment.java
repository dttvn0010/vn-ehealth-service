package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;

public class BaseAttachment extends BaseType {
    public String contentType;
    public String language;
    public byte[] data;
    public String url;
    public Integer size;
    public byte[] hash;
    public String title;
    public Date creation;
}
