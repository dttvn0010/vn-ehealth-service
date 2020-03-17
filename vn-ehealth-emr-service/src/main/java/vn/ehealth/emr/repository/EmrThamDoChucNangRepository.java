package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrThamDoChucNang;

public interface EmrThamDoChucNangRepository extends MongoRepository<EmrThamDoChucNang, ObjectId> {

    public List<EmrThamDoChucNang> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrThamDoChucNang> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
    public Optional<EmrThamDoChucNang> findByIdhis(String idhis);
}
