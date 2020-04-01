package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.DonThuocRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class DonThuocService {

    @Autowired private DonThuocRepository donThuocRepository;
    
    @Autowired LogService logService;
    
    public Optional<DonThuoc> getById(ObjectId id) {
        return donThuocRepository.findById(id);
    }
    
    public List<DonThuoc> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return donThuocRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<DonThuoc> dtList, String jsonSt) {
        for(int i = 0; i < dtList.size(); i++) {
            var dt = dtList.get(i);
            if(dt.idhis != null) {
                dt.id = donThuocRepository.findByIdhis(dt.idhis).map(x -> x.id).orElse(null);
            }
            var check = dt.id;
            dt.hoSoBenhAnId = hsba.id;
            dt.benhNhanId = hsba.benhNhanId;
            dt.coSoKhamBenhId = hsba.coSoKhamBenhId;
            dt = donThuocRepository.save(dt);
            dtList.set(i, dt);
            if(check == null) {
                logService.logAction(DonThuoc.class.getName(), dt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(dt), "");
            } else {
            	logService.logAction(DonThuoc.class.getName(), dt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(dt), "");
            } 
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".EmrDonThuocList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}
