package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrXetNghiemDichVu;

public interface EmrXetNghiemDichVuRepository extends MongoRepository<EmrXetNghiemDichVu, ObjectId> {
    
    public List<EmrXetNghiemDichVu> findByEmrXetNghiemId(ObjectId emrXetNghiemId);

}
