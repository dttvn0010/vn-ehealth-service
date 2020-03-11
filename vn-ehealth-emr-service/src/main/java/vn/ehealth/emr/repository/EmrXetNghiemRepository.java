package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrXetNghiem;

public interface EmrXetNghiemRepository extends MongoRepository<EmrXetNghiem, ObjectId> {

    public List<EmrXetNghiem> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);
}
