package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ChamSoc;

public interface ChamSocRepository extends MongoRepository<ChamSoc, ObjectId>  {

    public Optional<ChamSoc> findByIdhis(String idhis);
    public List<ChamSoc> findByHoSoBenhAnRefObjectIdAndTrangThai(ObjectId hsbaId, int trangThai);
}
