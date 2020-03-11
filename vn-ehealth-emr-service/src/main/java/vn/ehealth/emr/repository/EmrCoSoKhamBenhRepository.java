package vn.ehealth.emr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrCoSoKhamBenh;

public interface EmrCoSoKhamBenhRepository extends MongoRepository<EmrCoSoKhamBenh, ObjectId> {

    public Optional<EmrCoSoKhamBenh> findByMa(String ma);
}
