package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.DonThuoc;

public interface DonThuocRepository extends MongoRepository<DonThuoc, ObjectId> {
     
    public List<DonThuoc> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public List<DonThuoc> findByBenhNhanIdAndTrangThai(ObjectId benhNhanId, int trangThai);
    public Optional<DonThuoc> findByIdhis(String idhis);
}
