package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrHoiChan;

public interface EmrHoiChanRepository extends MongoRepository<EmrHoiChan, ObjectId> {

    public List<EmrHoiChan> findByEmrVaoKhoaId(ObjectId emrVaoKhoaId);
}
