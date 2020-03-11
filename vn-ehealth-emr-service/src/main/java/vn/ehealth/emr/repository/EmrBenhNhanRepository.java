package vn.ehealth.emr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrBenhNhan;

public interface EmrBenhNhanRepository extends MongoRepository<EmrBenhNhan, ObjectId> {

    public Optional<EmrBenhNhan> findByIddinhdanhchinh(String iddinhdanhchinh);
}
