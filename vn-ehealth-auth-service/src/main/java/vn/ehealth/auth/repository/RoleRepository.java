package vn.ehealth.auth.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.auth.model.Role;

public interface RoleRepository extends MongoRepository<Role, ObjectId>  {
    
    Optional<Role> findByMa(String ma);
}
