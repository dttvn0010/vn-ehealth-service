package vn.ehealth.emr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrThanhVienHoiChan;

public interface EmrThanhVienHoiChanRepository extends MongoRepository<EmrThanhVienHoiChan, ObjectId> {
    
    public List<EmrThanhVienHoiChan> findByEmrHoiChanId(ObjectId emrHoiChanId);

}
