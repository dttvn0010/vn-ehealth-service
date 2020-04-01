package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.HoiChan;
import vn.ehealth.cdr.repository.HoiChanRepository;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class HoiChanService {

    @Autowired private HoiChanRepository hoiChanRepository;
    
    @Autowired private LogService logService;
    
    public Optional<HoiChan> getById(ObjectId id) {
        return hoiChanRepository.findById(id);
    }
    
    public List<HoiChan> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return hoiChanRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<HoiChan> hcList, @Nonnull List<Object> hcObjList, String jsonSt) {
        for(int i = 0; i < hcList.size(); i++) {
            var hc = hcList.get(i);
            if(hc.idhis != null) {
            	hc.id = hoiChanRepository.findByIdhis(hc.idhis).map(x -> x.id).orElse(null);
            }
            
            hc.hoSoBenhAnId = hsba.id;
            hc.benhNhanId = hsba.benhNhanId;
            hc.coSoKhamBenhId = hsba.coSoKhamBenhId;
            hc = hoiChanRepository.save(hc);
            hcList.set(i, hc);
        }
        
        logService.logAction(HoSoBenhAn.class.getName() + ".EmrHoiChanList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);        
    }
}
