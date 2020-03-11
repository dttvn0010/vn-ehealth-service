package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrPhauThuatThuThuat;

public interface EmrPhauThuatThuThuatRepository extends MongoRepository<EmrPhauThuatThuThuat, ObjectId> {
    
    public List<EmrPhauThuatThuThuat> findByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId);

}
