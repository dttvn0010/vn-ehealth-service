package vn.ehealth.cdr.repository;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ChanDoanHinhAnh;

public interface ChanDoanHinhAnhRepository extends MongoRepository<ChanDoanHinhAnh, ObjectId> {
    public Optional<ChanDoanHinhAnh> findByIdhis(String idhis);
    public List<ChanDoanHinhAnh> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
}
