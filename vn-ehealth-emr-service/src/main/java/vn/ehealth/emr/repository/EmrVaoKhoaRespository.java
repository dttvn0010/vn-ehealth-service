package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrVaoKhoa;

public interface EmrVaoKhoaRespository extends MongoRepository<EmrVaoKhoa, ObjectId> {

    public List<EmrVaoKhoa> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId, Sort sort);
}
