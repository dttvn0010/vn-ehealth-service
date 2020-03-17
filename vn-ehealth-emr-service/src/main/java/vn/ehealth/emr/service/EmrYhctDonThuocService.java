package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrYhctDonThuoc;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.repository.EmrYhctDonThuocRepository;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrYhctDonThuocService {

    @Autowired
    private EmrYhctDonThuocRepository emrYhctDonThuocRepository;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public List<EmrYhctDonThuoc> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrYhctDonThuocRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
  
    public List<EmrYhctDonThuoc> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrYhctDonThuocRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrYhctDonThuoc createOrUpdate(@Nonnull EmrYhctDonThuoc donthuoc) {
        if(donthuoc.id == null && donthuoc.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(donthuoc.emrHoSoBenhAnId).orElseThrow();
            donthuoc.emrBenhNhanId = hsba.emrBenhNhanId;
            donthuoc.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        
        return emrYhctDonThuocRepository.save(donthuoc);
    }
    
    public void delete(ObjectId id) {
        var donthuoc = emrYhctDonThuocRepository.findById(id);
        donthuoc.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrYhctDonThuocRepository.save(x);
        });
    }
}
