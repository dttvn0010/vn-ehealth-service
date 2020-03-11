package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.r4.model.Attachment;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseAttachment {
    public String contentType;
    public String language;
    public String data;
    public String url;
    public Integer size;
    public String hash;
    public String title;
    public Date creation;
  
    
    public static BaseAttachment fromAttachment(Attachment object) {
        if(object == null) {
            return null;
        }
        var entity = new BaseAttachment();
        
        entity.contentType = object.getContentType();
        entity.language = object.getLanguage();
        
        if(object.getData() != null) {
            entity.data = new String(Base64.encodeBase64(object.getData()), ca.uhn.fhir.rest.api.Constants.CHARSET_UTF8);
        }
        
        entity.url = object.getUrl();
        entity.size = object.getSize();
        
        if(object.getHash() != null) {
            entity.hash = new String(Base64.encodeBase64(object.getHash()), ca.uhn.fhir.rest.api.Constants.CHARSET_UTF8);
        }
        
        entity.title = object.getTitle();
        entity.creation = object.getCreation();
        return entity;
    }
    
    public static List<BaseAttachment> fromAttachmentList(List<Attachment> lst) {
        return transform(lst, x -> fromAttachment(x));
    }
    
    public static Attachment toAttachment(BaseAttachment entity) {
        if(entity == null) {
            return null;
        }
        
        var object = new Attachment();
        object.setContentType(entity.contentType);
        object.setLanguage(entity.language);
        
        if(entity.data != null) {
            object.setData(Base64.decodeBase64(entity.data.getBytes(ca.uhn.fhir.rest.api.Constants.CHARSET_UTF8)));
        }
        
        object.setUrl(entity.url);
        object.setSize(entity.size);
        
        if(entity.hash != null) {
            object.setHash(Base64.decodeBase64(entity.hash.getBytes(ca.uhn.fhir.rest.api.Constants.CHARSET_UTF8)));
        }
        
        object.setTitle(entity.title);
        object.setCreation(entity.creation);
        return object;
    }

    
    public static List<Attachment> toAttachmentList(List<BaseAttachment> entityList) {
        return transform(entityList, x -> toAttachment(x));        
    }
}
