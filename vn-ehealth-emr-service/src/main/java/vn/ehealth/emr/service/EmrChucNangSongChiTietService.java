package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrChucNangSongChiTiet;
import vn.ehealth.emr.repository.EmrChucNangSongChiTietRepository;

@Service
public class EmrChucNangSongChiTietService {

    @Autowired EmrChucNangSongChiTietRepository emrChucNangSongChiTietRepository;
    
    public List<EmrChucNangSongChiTiet> getByEmrChucNangSongId(ObjectId emrChucNangSongId) {
        return emrChucNangSongChiTietRepository.findByEmrChucNangSongId(emrChucNangSongId);                
    }
    
    public void deleteAllByChucNangSongId(ObjectId emrChucNangSongId) {
        for(var cnsct : getByEmrChucNangSongId(emrChucNangSongId)) {
            emrChucNangSongChiTietRepository.delete(cnsct);                    
        }
    }
    
    public EmrChucNangSongChiTiet createOrUpdate(@Nonnull EmrChucNangSongChiTiet emrChucNangSongChiTiet) {
        return emrChucNangSongChiTietRepository.save(emrChucNangSongChiTiet);
    }
}
