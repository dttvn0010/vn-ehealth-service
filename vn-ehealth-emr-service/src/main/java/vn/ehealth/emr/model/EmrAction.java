package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_action")
public class EmrAction {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }
    
    public String ma;
    
    public String ten;
    
    public String mota;

}
