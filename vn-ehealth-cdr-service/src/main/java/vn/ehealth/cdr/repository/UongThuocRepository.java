package vn.ehealth.cdr.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.UongThuoc;

public interface UongThuocRepository extends MongoRepository<UongThuoc, ObjectId> {

}
