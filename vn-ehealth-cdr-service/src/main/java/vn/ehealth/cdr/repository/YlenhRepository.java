package vn.ehealth.cdr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.Ylenh;

public interface YlenhRepository extends MongoRepository<Ylenh, ObjectId>{

    public List<Ylenh> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
}
