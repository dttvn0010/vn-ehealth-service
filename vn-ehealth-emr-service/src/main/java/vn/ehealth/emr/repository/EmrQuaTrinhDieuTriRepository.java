package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrQuaTrinhDieuTri;

public interface EmrQuaTrinhDieuTriRepository extends MongoRepository<EmrQuaTrinhDieuTri, ObjectId> {
    
    public List<EmrQuaTrinhDieuTri> findByEmrDieuTriId(ObjectId emrDieuTriId);

}
