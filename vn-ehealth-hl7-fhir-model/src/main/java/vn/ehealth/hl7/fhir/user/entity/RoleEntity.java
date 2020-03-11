package vn.ehealth.hl7.fhir.user.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;




@Document(collection = "authRole")
public class RoleEntity {
    
    @Id public String id;
    public String name;
    @DBRef public List<PermissionEntity> permissions;
    
    public static class Values {
        public final static String USER = "USER";
        public final static String ADMIN = "ADMIN";
    }
}
