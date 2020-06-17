package vn.ehealth.cdr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.HoSoBenhAn;

public interface HoSoBenhAnRepository extends MongoRepository<HoSoBenhAn, ObjectId> {
    Optional<HoSoBenhAn> findByMaYte(String maYte);
    Optional<HoSoBenhAn> findByMaTraoDoi(String maTraoDoi);
}
