package vn.ehealth.emr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrHoSoBenhAn;

public interface EmrHoSoBenhAnRepository extends MongoRepository<EmrHoSoBenhAn, ObjectId> {
    List<EmrHoSoBenhAn> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
    Optional<EmrHoSoBenhAn> findByMatraodoi(String matraodoi);
}
