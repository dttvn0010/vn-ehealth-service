package vn.ehealth.emr.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrHoiChan;
import vn.ehealth.emr.repository.EmrHoiChanRepository;

@Service
public class EmrHoiChanService {

    @Autowired EmrHoiChanRepository emrHoiChanRepository;
    @Autowired EmrThanhVienHoiChanService emrThanhVienHoiChanService;
    
    public List<EmrHoiChan> getByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        return emrHoiChanRepository.findByEmrVaoKhoaId(emrVaoKhoaId);
    }
    
    public void deleteAllByEmrVaoKhoaId(ObjectId emrVaoKhoaId) {
        for(var hoichan : getByEmrVaoKhoaId(emrVaoKhoaId)) {
            emrHoiChanRepository.delete(hoichan);
        }
    }
    
    public EmrHoiChan createOrUpdate(@Nonnull EmrHoiChan emrHoiChan) {
        emrHoiChan = emrHoiChanRepository.save(emrHoiChan);
        
        emrThanhVienHoiChanService.deleteAllByEmrHoiChanId(emrHoiChan.id);
        
        for(int i = 0; emrHoiChan.emrThanhVienHoiChans != null && i < emrHoiChan.emrThanhVienHoiChans.size(); i++) {
            var tvhc = emrHoiChan.emrThanhVienHoiChans.get(i);
            tvhc.emrHoiChanId = emrHoiChan.id;
            tvhc.emrVaoKhoaId = emrHoiChan.emrVaoKhoaId;
            tvhc.emrHoSoBenhAnId = emrHoiChan.emrHoSoBenhAnId;
            tvhc.emrBenhNhanId = emrHoiChan.emrBenhNhanId;
            tvhc.emrCoSoKhamBenhId = emrHoiChan.emrCoSoKhamBenhId;
            tvhc = emrThanhVienHoiChanService.createOrUpdate(tvhc);
            emrHoiChan.emrThanhVienHoiChans.set(i, tvhc);
        }
        
        
        return emrHoiChanRepository.save(emrHoiChan);
    }
    
    public void delete(ObjectId id) {
        emrHoiChanRepository.deleteById(id);
    }
}
