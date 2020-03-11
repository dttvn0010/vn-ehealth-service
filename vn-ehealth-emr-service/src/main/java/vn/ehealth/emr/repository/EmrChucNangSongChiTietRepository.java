package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChucNangSongChiTiet;

public interface EmrChucNangSongChiTietRepository extends MongoRepository<EmrChucNangSongChiTiet, ObjectId> {
    
    public List<EmrChucNangSongChiTiet> findByEmrChucNangSongId(ObjectId emrChucNangSongId);    

}
