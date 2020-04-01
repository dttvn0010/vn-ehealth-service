package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.GiaiPhauBenh;

public interface GiaiPhauBenhRepository extends MongoRepository<GiaiPhauBenh, ObjectId> {

    public List<GiaiPhauBenh> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<GiaiPhauBenh> findByIdhis(String idhis);
}
