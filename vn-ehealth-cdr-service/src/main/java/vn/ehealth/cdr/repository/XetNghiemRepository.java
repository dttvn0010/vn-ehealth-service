package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.XetNghiem;

public interface XetNghiemRepository extends MongoRepository<XetNghiem, ObjectId> {
    public Optional<XetNghiem> findByIdhis(String idhis);
    public List<XetNghiem> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public List<XetNghiem> findByBenhNhanIdAndTrangThai(ObjectId benhNhanId, int trangThai);
}
