package vn.ehealth.emr.repository;


import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import vn.ehealth.emr.model.EmrChucNangSong;

public interface EmrChucNangSongRepository extends MongoRepository<EmrChucNangSong, ObjectId> {

    public List<EmrChucNangSong> findByEmrHoSoBenhAnIdAndTrangThai(ObjectId emrHoSoBenhAnId, int trangThai);
    public List<EmrChucNangSong> findByEmrBenhNhanIdAndTrangThai(ObjectId emrBenhNhanId, int trangThai);
    public Optional<EmrChucNangSong> findByIdhis(String idhis);
}
