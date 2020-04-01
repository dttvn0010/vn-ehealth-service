package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.DieuTri;

public interface DieuTriRepository extends MongoRepository<DieuTri, ObjectId> {

    public List<DieuTri> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<DieuTri> findByIdhis(String idhis);
}
