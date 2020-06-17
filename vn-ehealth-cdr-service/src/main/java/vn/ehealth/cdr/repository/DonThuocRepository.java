package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.DonThuoc;

public interface DonThuocRepository extends MongoRepository<DonThuoc, ObjectId> {
    
    public Optional<DonThuoc> findByIdhis(String idhis);
    public List<DonThuoc> findByHoSoBenhAnRefObjectIdAndTrangThai(ObjectId hsbaId, int trangThai);
    public List<DonThuoc> findByYlenhRefObjectIdAndTrangThai(ObjectId ylenhId, int trangThai);
}
