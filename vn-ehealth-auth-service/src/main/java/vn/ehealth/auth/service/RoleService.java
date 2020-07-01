package vn.ehealth.auth.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import vn.ehealth.auth.model.Role;
import vn.ehealth.auth.repository.RoleRepository;

import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public Optional<Role> getById(ObjectId id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> getByMa(String ma) {
        return roleRepository.findByMa(ma);
    }

    public List<Role> getAll() {
        return roleRepository.findAll();
    }
    
    public List<Role> search(String keyword) {
    	var criteria = new Criteria().orOperator(
                Criteria.where("ma").regex(keyword),
                Criteria.where("ten").regex(keyword)
            );

    	var query = new Query(criteria);
    	return mongoTemplate.find(query, Role.class);
    	
    }
    
    public Role save(Role role) {
        return roleRepository.save(role);
    }
    
}
