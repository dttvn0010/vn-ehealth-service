package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import vn.ehealth.emr.model.EmrDonThuoc;

public interface EmrDonThuocRepository extends MongoRepository<EmrDonThuoc, ObjectId> {
     
    public List<EmrDonThuoc> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrDonThuoc> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
    public Optional<EmrDonThuoc> findByIdhis(String idhis);
}
