package vn.ehealth.cdr.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.Ylenh;

public interface YlenhRepository extends MongoRepository<Ylenh, ObjectId>{

}
