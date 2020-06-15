package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ChamSocOld;

public interface ChamSocOldRepository extends MongoRepository<ChamSocOld, ObjectId> {

	public Optional<ChamSocOld> findByIdhis(String idhis);
    public List<ChamSocOld> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
}
