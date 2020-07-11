package vn.ehealth.cdr.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.CoSoKhamBenh;

public interface CoSoKhamBenhRepository extends MongoRepository<CoSoKhamBenh, ObjectId> {

}
