package vn.ehealth.auth.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import vn.ehealth.auth.model.Role;
import vn.ehealth.auth.repository.RoleRepository;

import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Optional<Role> getById(ObjectId id) {
        return roleRepository.findById(id);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}
