package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrPhauThuatThuThuat;

public interface EmrPhauThuatThuThuatRepository extends MongoRepository<EmrPhauThuatThuThuat, ObjectId> {
    public Optional<EmrPhauThuatThuThuat> findByIdhis(String idhis);
    public List<EmrPhauThuatThuThuat> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrPhauThuatThuThuat> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);

}
