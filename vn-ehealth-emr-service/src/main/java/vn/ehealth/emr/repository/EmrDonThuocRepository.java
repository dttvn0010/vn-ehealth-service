package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrDonThuoc;

public interface EmrDonThuocRepository extends MongoRepository<EmrDonThuoc, ObjectId> {
     
    public List<EmrDonThuoc> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);;
}
