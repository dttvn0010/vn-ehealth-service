package vn.ehealth.cdr.repository;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.cdr.model.ChucNangSong;

public interface ChucNangSongRepository extends MongoRepository<ChucNangSong, ObjectId> {

    public List<ChucNangSong> findByHoSoBenhAnIdAndTrangThai(ObjectId hoSoBenhAnId, int trangThai);
    public Optional<ChucNangSong> findByIdhis(String idhis);
}
