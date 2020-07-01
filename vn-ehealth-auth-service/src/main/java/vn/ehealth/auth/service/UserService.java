package vn.ehealth.auth.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.ehealth.auth.model.User;
import vn.ehealth.auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<User> getById(ObjectId id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    
    public List<User> search(String keyword) {
    	var criteria = new Criteria().orOperator(
                Criteria.where("username").regex(keyword),
                Criteria.where("tenDayDu").regex(keyword)
            );

    	var query = new Query(criteria);
    	return mongoTemplate.find(query, User.class);
    	
    }
}
