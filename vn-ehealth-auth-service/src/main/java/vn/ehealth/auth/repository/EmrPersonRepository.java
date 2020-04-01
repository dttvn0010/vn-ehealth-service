package vn.ehealth.auth.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.auth.model.EmrPerson;

import java.util.List;

public interface EmrPersonRepository extends MongoRepository<EmrPerson, ObjectId> {
    List<EmrPerson> findByEmailOrSodienthoaiOrTendaydu(String email, String sodienthoai, String tenDayDu);
}
