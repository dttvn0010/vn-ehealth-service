package vn.ehealth.emr.service;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.User;
import vn.ehealth.emr.repository.UserRepository;

@Service
public class UserService {

    @Autowired UserRepository userRepository;
    
    public Optional<User> getById(ObjectId id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
