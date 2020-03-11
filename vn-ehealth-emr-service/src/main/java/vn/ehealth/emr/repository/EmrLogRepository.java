package vn.ehealth.emr.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrLog;

public interface EmrLogRepository extends MongoRepository<EmrLog, ObjectId> {
    
}
