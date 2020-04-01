package vn.ehealth.auth.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.auth.service.EmrServiceFactory;

@JsonInclude(Include.NON_NULL)
@Document(collection = "user")
public class User {
    
    @Id public ObjectId id;
    public ObjectId coSoKhamBenhId;
    
    public String username;
    public String password;
    public String fullName;
    public ObjectId emrPersonId;
    @Transient public List<Role> roles;
    
    public List<ObjectId> roleIds;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
    
    public List<Role> getRoles() {
        if(roles == null) {
            roles = new ArrayList<Role>();
            if(roleIds != null) {
                for(var roleId : roleIds) {
                    var role = EmrServiceFactory.getRoleService().getById(roleId);
                    role.ifPresent(x -> roles.add(x));
                }
            }            
        }    
        return roles;
    }

}
