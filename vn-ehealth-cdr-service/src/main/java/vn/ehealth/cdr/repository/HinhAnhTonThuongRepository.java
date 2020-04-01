package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.HinhAnhTonThuong;

public interface HinhAnhTonThuongRepository extends MongoRepository<HinhAnhTonThuong, ObjectId> {

    public List<HinhAnhTonThuong> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<HinhAnhTonThuong> findByIdhis(String idhis);
}
