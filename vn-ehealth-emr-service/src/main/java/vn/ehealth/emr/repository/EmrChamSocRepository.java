package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChamSoc;

public interface EmrChamSocRepository extends MongoRepository<EmrChamSoc, ObjectId> {

    public List<EmrChamSoc> findByEmrVaoKhoaId(ObjectId emrVaoKhoaId);
}
