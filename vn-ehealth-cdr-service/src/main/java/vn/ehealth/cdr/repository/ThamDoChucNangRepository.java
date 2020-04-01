package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ThamDoChucNang;

public interface ThamDoChucNangRepository extends MongoRepository<ThamDoChucNang, ObjectId> {

    public List<ThamDoChucNang> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<ThamDoChucNang> findByIdhis(String idhis);
}
