package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.DichVuKyThuat;

public interface DichVuKyThuatRepository extends MongoRepository<DichVuKyThuat, ObjectId> {

    public Optional<DichVuKyThuat> findByIdhis(String idhis);
    public List<DichVuKyThuat> findByHoSoBenhAnRefObjectIdAndDmLoaiDVKTMa(String hsbaId, String maLoaiDVKT);
}
