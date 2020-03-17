package vn.ehealth.emr.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import vn.ehealth.emr.model.EmrPerson;

public interface EmrPersonRepository extends MongoRepository<EmrPerson, ObjectId> {
}
