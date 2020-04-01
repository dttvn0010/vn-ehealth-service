package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.HoiChan;

public interface HoiChanRepository extends MongoRepository<HoiChan, ObjectId> {
    
    public List<HoiChan> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<HoiChan> findByIdhis(String idhis);
}
