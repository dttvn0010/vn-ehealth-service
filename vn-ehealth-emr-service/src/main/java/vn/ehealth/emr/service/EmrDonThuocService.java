package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrDonThuoc;
import vn.ehealth.emr.repository.EmrDonThuocRepository;

@Service
public class EmrDonThuocService {

    @Autowired EmrDonThuocRepository emrDonThuocRepository;
    @Autowired EmrDonThuocChiTietService emrDonThuocChiTietService;
    
    public List<EmrDonThuoc> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrDonThuocRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var donthuoc : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrDonThuocRepository.delete(donthuoc);
        }
    }
    
    public EmrDonThuoc createOrUpdate(@Nonnull EmrDonThuoc emrDonThuoc) {
        emrDonThuoc = emrDonThuocRepository.save(emrDonThuoc);
                
        emrDonThuocChiTietService.deleteAllByDonThuocId(emrDonThuoc.id);
        
        for(int i = 0; emrDonThuoc.emrDonThuocChiTiets != null && i < emrDonThuoc.emrDonThuocChiTiets.size(); i++) {
            var dtct = emrDonThuoc.emrDonThuocChiTiets.get(i);
            dtct.emrDonThuocId = emrDonThuoc.id;
            dtct.emrHoSoBenhAnId = emrDonThuoc.emrHoSoBenhAnId;
            dtct.emrBenhNhanId = emrDonThuoc.emrBenhNhanId;
            dtct.emrCoSoKhamBenhId = emrDonThuoc.emrCoSoKhamBenhId;
            dtct = emrDonThuocChiTietService.createOrUpdate(dtct);
            emrDonThuoc.emrDonThuocChiTiets.set(i, dtct);
        }
         
        return emrDonThuoc;
    }
    
    public void delete(ObjectId id) {
        emrDonThuocRepository.deleteById(id);
    }
}
