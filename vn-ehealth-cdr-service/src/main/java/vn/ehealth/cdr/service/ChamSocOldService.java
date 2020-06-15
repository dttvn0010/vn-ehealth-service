package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChamSocOld;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChamSocOldRepository;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChamSocOldService {

    @Autowired private ChamSocOldRepository chamSocRepository;
    
    @Autowired private LogService logService;
    
    public Optional<ChamSocOld> getById(ObjectId id) {
        return chamSocRepository.findById(id);
    }
    
    public List<ChamSocOld> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chamSocRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(@Nonnull HoSoBenhAn hsba, @Nonnull List<ChamSocOld> csList, List<Object> csObjList, String jsonSt) {
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
