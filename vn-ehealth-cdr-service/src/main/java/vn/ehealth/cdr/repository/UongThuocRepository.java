package vn.ehealth.cdr.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.UongThuoc;

public interface UongThuocRepository extends MongoRepository<UongThuoc, ObjectId> {

    List<UongThuoc> findByChamSocRefObjectIdAndTrangThai(ObjectId chamSocId, int trangThai);;
}
