package vn.ehealth.cdr.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.CanboYte;

public interface CanboYteRepository extends MongoRepository<CanboYte, ObjectId> {
    
    public Optional<CanboYte> findByChungChiHanhNghe(String chungChiHanhNghe);
}
