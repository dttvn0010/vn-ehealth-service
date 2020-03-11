package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrDonThuocChiTiet;

public interface EmrDonThuocChiTietRepository extends MongoRepository<EmrDonThuocChiTiet, ObjectId> {
    
    List<EmrDonThuocChiTiet> findByEmrDonThuocId(ObjectId emrDonThuocId);
}
