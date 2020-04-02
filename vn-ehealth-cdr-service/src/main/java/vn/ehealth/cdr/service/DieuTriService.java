package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.DieuTriRepository;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class DieuTriService {
    
    @Autowired private DieuTriRepository dieuTriRepository;
    
    @Autowired private LogService logService;
    
    public Optional<DieuTri> getById(ObjectId id) {
        return dieuTriRepository.findById(id);
    }
    
    public List<DieuTri> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return dieuTriRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);        
    }
        
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<DieuTri> dtList, @Nonnull List<Object> dtObjList, String jsonSt) {
        for(int i = 0; i < dtList.size(); i++) {
            var dt = dtList.get(i);
            if(dt.idhis != null) {
            	dt.id = dieuTriRepository.findByIdhis(dt.idhis).map(x -> x.id).orElse(null);
            }
            
            dt.hoSoBenhAnId = hsba.id;
            dt.benhNhanId = hsba.benhNhanId;
            dt.coSoKhamBenhId = hsba.coSoKhamBenhId;
            dt = dieuTriRepository.save(dt);
            dtList.set(i, dt);
        }         
        logService.logAction(HoSoBenhAn.class.getName() + ".DieuTriList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}
