package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChamSoc;

public interface EmrChamSocRepository extends MongoRepository<EmrChamSoc, ObjectId> {

	public Optional<EmrChamSoc> findByIdhis(String idhis);
    public List<EmrChamSoc> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrChamSoc> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
}
