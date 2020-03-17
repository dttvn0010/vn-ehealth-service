package vn.ehealth.emr.repository;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;

public interface EmrChanDoanHinhAnhRepository extends MongoRepository<EmrChanDoanHinhAnh, ObjectId> {
    public Optional<EmrChanDoanHinhAnh> findByIdhis(String idhis);
    public List<EmrChanDoanHinhAnh> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrChanDoanHinhAnh> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
}
