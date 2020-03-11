package vn.ehealth.emr.repository;


import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;

public interface EmrChanDoanHinhAnhRepository extends MongoRepository<EmrChanDoanHinhAnh, ObjectId> {

    public List<EmrChanDoanHinhAnh> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);;
}
