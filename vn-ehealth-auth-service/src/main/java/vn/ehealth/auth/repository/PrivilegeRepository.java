package vn.ehealth.auth.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.auth.model.Privilege;


public interface PrivilegeRepository extends MongoRepository<Privilege, ObjectId> {
	
}
