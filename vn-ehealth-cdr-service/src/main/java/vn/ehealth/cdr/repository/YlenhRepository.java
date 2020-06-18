package vn.ehealth.cdr.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.Ylenh;

public interface YlenhRepository extends MongoRepository<Ylenh, ObjectId>{

    List<Ylenh> findByHoSoBenhAnRefObjectIdAndTrangThai(ObjectId hsbaId, int trangThai);
    
    List<Ylenh> findByHoSoBenhAnRefObjectIdAndTrangThai(ObjectId hsbaId, int trangThai, Pageable pageable);
    
    List<Ylenh> findByHoSoBenhAnRefObjectIdAndDmLoaiYlenhMaAndTrangThai(ObjectId hsbaId, String maLoaiYlenh, int trangThai, Pageable pageable);
    
    Optional<Ylenh> findByIdhis(String idhis);
}
