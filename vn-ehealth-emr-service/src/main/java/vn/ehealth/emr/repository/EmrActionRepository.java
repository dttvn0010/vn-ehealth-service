package vn.ehealth.emr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrAction;

public interface EmrActionRepository extends MongoRepository<EmrAction, ObjectId> {
    
    public Optional<EmrAction> findByMa(String ma);

}
