package vn.ehealth.emr.dto.request;

import java.util.List;

import vn.ehealth.emr.model.EmrPerson;

public class UserRequestDTO {
    public List<String> roleIds;
    public EmrPerson emrPerson;
}
