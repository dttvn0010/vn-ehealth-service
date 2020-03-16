package vn.ehealth.hl7.fhir.user.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authUser")
@CompoundIndex(def = "{'id':1,'username':1}", name = "index_by_default")
public class UserEntity {
    @Id public String id;
    public String username;
    public String password;
    
    @Transient public String passwordConfirm;
    @DBRef public List<RoleEntity> roles;
}
