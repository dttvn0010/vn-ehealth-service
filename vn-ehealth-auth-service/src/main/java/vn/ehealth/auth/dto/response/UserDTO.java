package vn.ehealth.auth.dto.response;

import java.util.List;

import vn.ehealth.auth.model.EmrPerson;
import vn.ehealth.auth.model.Role;

public class UserDTO {
    private String id;
    private String coSoKhamBenhId;
    private String username;
    private String password;
    private List<Role> roles;
    private EmrPerson emrPerson;
    
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCoSoKhamBenhId() {
		return coSoKhamBenhId;
	}
	
	public void setCoSoKhamBenhId(String coSoKhamBenhId) {
		this.coSoKhamBenhId = coSoKhamBenhId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public List<Role> getRoles() {
		return roles;
	}
	
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public EmrPerson getEmrPerson() {
		return emrPerson;
	}
	
	public void setEmrPerson(EmrPerson emrPerson) {
		this.emrPerson = emrPerson;
	}
        
}