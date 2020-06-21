package vn.ehealth.cdr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.DonThuocChiTiet;

public interface DonThuocChiTietRepository extends MongoRepository<DonThuocChiTiet, ObjectId> {

    public List<DonThuocChiTiet> findByDonThuocRefObjectIdAndTrangThai(ObjectId donThuocId, int trangThai);
}
