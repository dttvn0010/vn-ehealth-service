package vn.ehealth.hl7.fhir.core.entity;

import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 */
public class BaseAnnotation {
    @JsonIgnore public Type author;
    public Date time;
    public String text;
  
    public static BaseAnnotation fromAnnotation(Annotation object) {
        if(object == null) {
            return null;
        }
        
        var entity = new BaseAnnotation();
        
        entity.author = object.getAuthor();
        entity.time = object.getTime();
        entity.text = object.getText();
        
        return entity;
    }
    
    public static List<BaseAnnotation> fromAnnotationList(List<Annotation> lst) {
        return transform(lst, x -> fromAnnotation(x));
    }
    
    public static Annotation toAnnotation(BaseAnnotation entity) {
        if(entity == null) {
            return null;
        }
        
        var object = new Annotation();
        object.setAuthor(entity.author);
        object.setTime(entity.time);
        object.setText(entity.text);
        return object;
    }
    
    public static List<Annotation> toAnnotationList(List<BaseAnnotation> entityList){
        return transform(entityList, x -> toAnnotation(x));
    }
}
