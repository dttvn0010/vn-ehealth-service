package vn.ehealth.auth.dto.request;

import java.util.List;

import vn.ehealth.auth.model.EmrPerson;

public class UserRequestDTO {
    public List<String> roleIds;
    public EmrPerson emrPerson;
}
