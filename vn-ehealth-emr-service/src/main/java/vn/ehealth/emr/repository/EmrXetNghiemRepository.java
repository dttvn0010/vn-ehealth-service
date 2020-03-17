package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrXetNghiem;

public interface EmrXetNghiemRepository extends MongoRepository<EmrXetNghiem, ObjectId> {
    public Optional<EmrXetNghiem> findByIdhis(String idhis);
    public List<EmrXetNghiem> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrXetNghiem> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
}
