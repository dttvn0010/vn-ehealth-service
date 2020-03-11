package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrThanhVienHoiChan;
import vn.ehealth.emr.repository.EmrThanhVienHoiChanRepository;

@Service
public class EmrThanhVienHoiChanService {

    @Autowired EmrThanhVienHoiChanRepository emrThanhVienHoiChanRepository;
    
    public List<EmrThanhVienHoiChan> getByEmrHoiChanId(ObjectId emrHoiChanId) {
        return emrThanhVienHoiChanRepository.findByEmrHoiChanId(emrHoiChanId);
    }
    
    public void deleteAllByEmrHoiChanId(ObjectId emrHoiChanId) {
        for(var tvhc : getByEmrHoiChanId(emrHoiChanId)) {
            emrThanhVienHoiChanRepository.delete(tvhc);
        }
    }
    
    public EmrThanhVienHoiChan createOrUpdate(@Nonnull EmrThanhVienHoiChan emrThanhVienHoiChan) {
        return emrThanhVienHoiChanRepository.save(emrThanhVienHoiChan);
    }
}
