package vn.ehealth.emr.service;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import vn.ehealth.emr.model.Role;
import vn.ehealth.emr.repository.RoleRepository;

import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired RoleRepository roleRepository;
    
    public Optional<Role> getById(ObjectId id) {
        return roleRepository.findById(id);
    }
}
