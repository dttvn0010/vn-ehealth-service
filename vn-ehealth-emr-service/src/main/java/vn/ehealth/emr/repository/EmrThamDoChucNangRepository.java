package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrThamDoChucNang;

public interface EmrThamDoChucNangRepository extends MongoRepository<EmrThamDoChucNang, ObjectId> {

    public List<EmrThamDoChucNang> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);
}
