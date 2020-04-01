package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.PhauThuatThuThuat;

public interface PhauThuatThuThuatRepository extends MongoRepository<PhauThuatThuThuat, ObjectId> {
    public Optional<PhauThuatThuThuat> findByIdhis(String idhis);
    public List<PhauThuatThuThuat> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
}
