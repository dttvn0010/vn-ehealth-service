package vn.ehealth.cdr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ChamSoc;

public interface ChamSocRepository extends MongoRepository<ChamSoc, ObjectId>  {

    public List<ChamSoc> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
}
