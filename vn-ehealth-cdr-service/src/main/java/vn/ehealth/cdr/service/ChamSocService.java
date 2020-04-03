package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChamSocRepository;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class ChamSocService {

    @Autowired private ChamSocRepository chamSocRepository;
    
    @Autowired private LogService logService;
    
    public Optional<ChamSoc> getById(ObjectId id) {
        return chamSocRepository.findById(id);
    }
    
    public List<ChamSoc> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chamSocRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(@Nonnull HoSoBenhAn hsba, @Nonnull List<ChamSoc> csList, List<Object> csObjList, String jsonSt) {
        for(int i = 0; i < csList.size(); i++) {
            var cs = csList.get(i);
            if(cs.idhis != null) {
                cs.id = chamSocRepository.findByIdhis(cs.idhis).map(x -> x.id).orElse(null);
            }
            
            cs.hoSoBenhAnId = hsba.id;
            cs.benhNhanId = hsba.benhNhanId;
            cs.coSoKhamBenhId = hsba.coSoKhamBenhId;
            cs = chamSocRepository.save(cs);
            csList.set(i, cs);
           
        }      
        logService.logAction(HoSoBenhAn.class.getName() + ".ChamSocList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), null, 
                "", jsonSt);
    }
    
}
