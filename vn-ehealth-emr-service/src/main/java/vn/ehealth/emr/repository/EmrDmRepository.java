package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrDm;

public interface EmrDmRepository extends MongoRepository<EmrDm, ObjectId> {
    
    List<EmrDm> findByEmrNhomDmId(ObjectId emrNhomDmId);
    Optional<EmrDm> findByEmrNhomDmIdAndMa(ObjectId emrNhomDmId, String ma);
}
