package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrYhctDonThuocChiTiet;
import vn.ehealth.emr.repository.EmrYhctDonThuocChiTietRepository;

@Service
public class EmrYhctDonThuocChiTietService {
    
    @Autowired EmrYhctDonThuocChiTietRepository emrYhctDonThuocChiTietRepository;
    
    public List<EmrYhctDonThuocChiTiet> getByEmrYhctDonThuocId(ObjectId emrYhctDonThuocId) {
        return emrYhctDonThuocChiTietRepository.findByEmrYhctDonThuocId(emrYhctDonThuocId);
    }

    public void deleteAllByEmrYhctDonThuocId(ObjectId emrYhctDonThuocId) {
        for(var dtct : getByEmrYhctDonThuocId(emrYhctDonThuocId)) {
            emrYhctDonThuocChiTietRepository.delete(dtct);
        }
    }
    
    public EmrYhctDonThuocChiTiet createOrUpdate(@Nonnull EmrYhctDonThuocChiTiet emrYhctDonThuocChiTiet) {
        return emrYhctDonThuocChiTietRepository.save(emrYhctDonThuocChiTiet);
    }
}
