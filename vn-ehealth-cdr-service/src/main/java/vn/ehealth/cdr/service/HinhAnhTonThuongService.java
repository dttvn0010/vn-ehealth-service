package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HinhAnhTonThuong;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.HinhAnhTonThuongRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class HinhAnhTonThuongService {

    @Autowired private HinhAnhTonThuongRepository hinhAnhTonThuongRepository;
    
    @Autowired private LogService logService;
  
    public Optional<HinhAnhTonThuong> getById(ObjectId id) {
        return hinhAnhTonThuongRepository.findById(id);
    }
    
    public List<HinhAnhTonThuong> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return hinhAnhTonThuongRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var hatt = hinhAnhTonThuongRepository.findById(id);
        hatt.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            hinhAnhTonThuongRepository.save(x);
            logService.logAction(HinhAnhTonThuong.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }  
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<HinhAnhTonThuong> hattList, String jsonSt) {
        for(int i = 0; i < hattList.size(); i++) {
            var hatt = hattList.get(i);
            if(hatt.idhis != null) {
            	hatt.id = hinhAnhTonThuongRepository.findByIdhis(hatt.idhis).map(x -> x.id).orElse(null);
            }
            var check = hatt.id;
            hatt.hoSoBenhAnId = hsba.id;
            hatt.benhNhanId = hsba.benhNhanId;
            hatt.coSoKhamBenhId = hsba.coSoKhamBenhId;
            hatt = hinhAnhTonThuongRepository.save(hatt);
            hattList.set(i, hatt);
            if(check == null) {
                logService.logAction(HinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(hatt), "");
            } else {
            	logService.logAction(HinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(hatt), "");
            } 
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".HinhAnhTonThuongList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}
