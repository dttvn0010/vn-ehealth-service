package vn.ehealth.auth.model;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "user")
public class User {
    
    @Id public ObjectId id;
    public ObjectId coSoKhamBenhId;
    
    public String username;
    public String password;
    public String tenDayDu;
    public String email;
    public String soDienThoai;
    public String diaChi;
    
    public String canBoYteId;
    public String fhirPractitionerId;
    
    public String roleId;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
    
    @JsonIgnore
    public String getDisplay() {
        if(!StringUtils.isEmpty(tenDayDu)) {
            return tenDayDu;
        }
        return username;
    }
}
