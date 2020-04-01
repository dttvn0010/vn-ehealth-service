package vn.ehealth.auth.dto.request;

import java.util.List;

import vn.ehealth.auth.model.EmrPerson;

public class UserUpdateDTO {
    private String id;
    private List<String> roleIds;
    private EmrPerson emrPerson;
    
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getRoleIds() {
		return roleIds;
	}
	
	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}
	
	public EmrPerson getEmrPerson() {
		return emrPerson;
	}
	
	public void setEmrPerson(EmrPerson emrPerson) {
		this.emrPerson = emrPerson;
	}    
    
}
