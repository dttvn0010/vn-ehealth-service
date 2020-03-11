package vn.ehealth.emr.service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrYhctDonThuoc;
import vn.ehealth.emr.repository.EmrYhctDonThuocRepository;

@Service
public class EmrYhctDonThuocService {

    @Autowired EmrYhctDonThuocRepository emrYhctDonThuocRepository;
    @Autowired EmrYhctDonThuocChiTietService emrYhctDonThuocChiTietService;
    
    public List<EmrYhctDonThuoc> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrYhctDonThuocRepository.findByEmrHoSoBenhAnId(emrHoSoBenhAnId);
    }
    
    public void deleteAllByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        for(var yhctdt : getByEmrHoSoBenhAnId(emrHoSoBenhAnId)) {
            emrYhctDonThuocRepository.delete(yhctdt);
        }
    }
    
    public EmrYhctDonThuoc createOrUpdate(EmrYhctDonThuoc emrYhctDonThuoc) {
        emrYhctDonThuoc = emrYhctDonThuocRepository.save(emrYhctDonThuoc);
        
        for(int i = 0; emrYhctDonThuoc.emrYhctDonThuocChiTiets != null && i < emrYhctDonThuoc.emrYhctDonThuocChiTiets.size(); i++) {
            var dtct = emrYhctDonThuoc.emrYhctDonThuocChiTiets.get(i);
            dtct.emrYhctDonThuocId = emrYhctDonThuoc.id;
            dtct.emrHoSoBenhAnId = emrYhctDonThuoc.emrHoSoBenhAnId;
            dtct.emrBenhNhanId = emrYhctDonThuoc.emrBenhNhanId;
            dtct.emrCoSoKhamBenhId = emrYhctDonThuoc.emrCoSoKhamBenhId;
            dtct = emrYhctDonThuocChiTietService.createOrUpdate(dtct);
            emrYhctDonThuoc.emrYhctDonThuocChiTiets.set(i, dtct);
        }
        
        return emrYhctDonThuoc;
    }
    
    public void delete(ObjectId id) {
        emrYhctDonThuocRepository.deleteById(id);
    }
}
