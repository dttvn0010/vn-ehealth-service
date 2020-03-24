package vn.ehealth.emr.dto.response;

import vn.ehealth.emr.model.EmrPerson;
import vn.ehealth.emr.model.Role;

import java.util.List;

public class UserDTO {
    private String id;
    private String emrCoSoKhamBenhId;
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
	
	public String getEmrCoSoKhamBenhId() {
		return emrCoSoKhamBenhId;
	}
	
	public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
		this.emrCoSoKhamBenhId = emrCoSoKhamBenhId;
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