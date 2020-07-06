package vn.ehealth.auth.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "privilege")
public class Privilege {

    @Id public ObjectId id;
    
    public String code;
    public String name;
    public String description;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
}
