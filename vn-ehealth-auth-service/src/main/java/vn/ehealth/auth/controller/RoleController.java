package vn.ehealth.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.model.Role;
import vn.ehealth.auth.service.RoleService;

import java.util.List;

@RequestMapping("api/role")
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("getAll")
    @ResponseBody
    public List<Role> getAll() {
        return roleService.getAll();
    }
}
