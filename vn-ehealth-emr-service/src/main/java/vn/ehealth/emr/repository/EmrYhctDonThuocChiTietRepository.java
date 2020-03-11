package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrYhctDonThuocChiTiet;

public interface EmrYhctDonThuocChiTietRepository extends MongoRepository<EmrYhctDonThuocChiTiet, ObjectId> {

    List<EmrYhctDonThuocChiTiet> findByEmrYhctDonThuocId(ObjectId emrYhctDonThuocId);
}
