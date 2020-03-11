package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrGiaiPhauBenh;

public interface EmrGiaiPhauBenhRepository extends MongoRepository<EmrGiaiPhauBenh, ObjectId> {

    public List<EmrGiaiPhauBenh> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);
}
