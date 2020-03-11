package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrDonThuocChiTiet;
import vn.ehealth.emr.repository.EmrDonThuocChiTietRepository;

@Service
public class EmrDonThuocChiTietService {
    
    @Autowired EmrDonThuocChiTietRepository emrDonThuocChiTietRepository;
    
    public List<EmrDonThuocChiTiet> getByEmrDonThuocId(ObjectId emrDonThuocId) {
        return emrDonThuocChiTietRepository.findByEmrDonThuocId(emrDonThuocId);
    }
    
    public void deleteAllByDonThuocId(ObjectId emrDonThuocId) {
        for(var dtct : getByEmrDonThuocId(emrDonThuocId)) {
            emrDonThuocChiTietRepository.delete(dtct);
        }
    }
    
    public EmrDonThuocChiTiet createOrUpdate(@Nonnull EmrDonThuocChiTiet emrDonThuocChiTiet) {
        return emrDonThuocChiTietRepository.save(emrDonThuocChiTiet);
    }
}
