package vn.ehealth.auth.model;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    public String tenDayDu;
    public String email;
    public String soDienThoai;
    public String diaChi;
    
    public String chungChiHanhNghe;
    public String fhirPractitionerId;
    
    public ObjectId roleId;
    
    @Transient private Role role;
    
    public String getId() {
        return id != null? id.toHexString() : null;
    }
    
    public void setId(String id) {
        if(id != null) {
            this.id = new ObjectId(id);
        }
    }
    
    public String getRoleId() {
        return roleId != null? roleId.toHexString() : null;
    }
    
    public void setRoleId(String roleId) {
        if(roleId != null) {
            this.roleId = new ObjectId(roleId);
        }
    }
    
    public String getCoSoKhamBenhId() {
        return coSoKhamBenhId != null? coSoKhamBenhId.toHexString() : null;
    }
    
    public void setCoSoKhamBenhId(String coSoKhamBenhId) {
        if(coSoKhamBenhId != null) {
            this.coSoKhamBenhId = new ObjectId(coSoKhamBenhId);
        }
    }
    
    @JsonIgnore
    public String getDisplay() {
        if(!StringUtils.isEmpty(tenDayDu)) {
            return tenDayDu;
        }
        return username;
    }
    
    public Role getRole() {
        if(role == null && roleId != null) {
            role = EmrServiceFactory.getRoleService().getById(roleId).orElse(null);
        }
        return role;
    }
}
