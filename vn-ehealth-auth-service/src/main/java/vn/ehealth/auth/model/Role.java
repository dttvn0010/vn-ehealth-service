package vn.ehealth.auth.model;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "role")
public class Role {
	final public static String ADMIN = "ADMIN";
    
    @Id public ObjectId id;
    
    public String code;
    public String name;
    
    public Set<String> privileges;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
    
    public boolean isAdminRole() {
    	return ADMIN.equals(this.code);
    	
    }
    
    public boolean hasPrivilege(String privilegeCode) {
    	return privileges != null && privilegeCode.contains(privilegeCode);
    }
}
