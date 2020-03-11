package vn.ehealth.emr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrNhomDm;

public interface EmrNhomDmRepository extends MongoRepository<EmrNhomDm, ObjectId> {
    
    public Optional<EmrNhomDm> findByMa(String ma);

}
