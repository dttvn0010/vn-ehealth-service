package vn.ehealth.cdr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.BenhNhan;

public interface BenhNhanRepository extends MongoRepository<BenhNhan, ObjectId> {

    public Optional<BenhNhan> findByIdDinhDanhChinh(String idDinhDanhChinh);
    public Optional<BenhNhan> findByIdhis(String idhis);
}
