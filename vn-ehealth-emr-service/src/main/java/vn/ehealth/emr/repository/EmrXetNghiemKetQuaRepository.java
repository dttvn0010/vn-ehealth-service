package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrXetNghiemKetQua;

public interface EmrXetNghiemKetQuaRepository extends MongoRepository<EmrXetNghiemKetQua, ObjectId> {
    
    public List<EmrXetNghiemKetQua> findByEmrXetNghiemDichVuId(ObjectId emrXetNghiemDichVuId);

}
