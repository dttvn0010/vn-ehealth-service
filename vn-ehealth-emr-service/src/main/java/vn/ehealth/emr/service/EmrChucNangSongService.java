package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrChucNangSong;
import vn.ehealth.emr.repository.EmrChucNangSongRepository;

@Service
public class EmrChucNangSongService {

    @Autowired EmrChucNangSongRepository emrChucNangSongRepository;
    @Autowired EmrChucNangSongChiTietService emrChucNangSongChiTietService;
    
    public List<EmrChucNangSong> getByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        return emrChucNangSongRepository.findByEmrVaoKhoaId(emrVaoKhoaId);
    }
    
    public void deleteAllByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        for(var cns : getByEmrVaoKhoaId(emrVaoKhoaId)) {
            emrChucNangSongRepository.delete(cns);
        }
    }
    
    public EmrChucNangSong createOrUpdate(@Nonnull EmrChucNangSong emrChucNangSong) {
        emrChucNangSong = emrChucNangSongRepository.save(emrChucNangSong);
        
        emrChucNangSongChiTietService.deleteAllByChucNangSongId(emrChucNangSong.id);
        
        for(int i = 0; emrChucNangSong.emrChucNangSongChiTiets != null && i < emrChucNangSong.emrChucNangSongChiTiets.size(); i++) {
            var cnsct = emrChucNangSong.emrChucNangSongChiTiets.get(i);
            cnsct.emrChucNangSongId = emrChucNangSong.id;            
            cnsct.emrVaoKhoaId = emrChucNangSong.emrVaoKhoaId;
            cnsct.emrHoSoBenhAnId = emrChucNangSong.emrHoSoBenhAnId;
            cnsct.emrBenhNhanId = emrChucNangSong.emrBenhNhanId;
            cnsct.emrCoSoKhamBenhId = emrChucNangSong.emrCoSoKhamBenhId;
            cnsct = emrChucNangSongChiTietService.createOrUpdate(cnsct);
            emrChucNangSong.emrChucNangSongChiTiets.set(i, cnsct);
        }
        
        return emrChucNangSongRepository.save(emrChucNangSong);
    }
    
    public void delete(ObjectId id) {
        emrChucNangSongRepository.deleteById(id);
    }
}
