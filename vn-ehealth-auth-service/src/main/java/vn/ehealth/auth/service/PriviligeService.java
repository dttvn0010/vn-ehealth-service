package vn.ehealth.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.auth.model.Privilege;
import vn.ehealth.auth.repository.PrivilegeRepository;

@Service
public class PriviligeService {

	@Autowired 
	PrivilegeRepository privilegeRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	
	public Optional<Privilege> getByCode(String code) {
        return privilegeRepository.findByCode(code);
    }
	
	public List<Privilege> search(String keyword) {
    	var criteria = new Criteria().orOperator(
                Criteria.where("code").regex(keyword),
                Criteria.where("name").regex(keyword)
            );

    	var query = new Query(criteria);
    	return mongoTemplate.find(query, Privilege.class);
    	
    }
    
    public Privilege save(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }
}
