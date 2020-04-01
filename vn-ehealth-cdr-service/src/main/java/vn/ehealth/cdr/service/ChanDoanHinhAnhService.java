package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.auth.service.UserService;
import vn.ehealth.cdr.model.ChanDoanHinhAnh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChanDoanHinhAnhRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class ChanDoanHinhAnhService {
	
	@Autowired 
    private ChanDoanHinhAnhRepository chanDoanHinhAnhRepository;
    
    @Autowired LogService logService;
    
    @Autowired UserService userService;
    
    public List<ChanDoanHinhAnh> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chanDoanHinhAnhRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<ChanDoanHinhAnh> cdhaList, String jsonSt) {
        for(int i = 0; i < cdhaList.size(); i++) {
            var cdha = cdhaList.get(i);
            if(cdha.idhis != null) {
                cdha.id = chanDoanHinhAnhRepository.findByIdhis(cdha.idhis).map(x -> x.id).orElse(null);
            }
            cdha.hoSoBenhAnId = hsba.id;
            cdha.benhNhanId = hsba.benhNhanId;
            cdha.coSoKhamBenhId = hsba.coSoKhamBenhId;
            cdha = chanDoanHinhAnhRepository.save(cdha);
            cdhaList.set(i, cdha);
            if(cdha.id == null) {
                logService.logAction(ChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(cdha), "");
            } else {
            	logService.logAction(ChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(cdha), "");
            }
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".EmrChanDoanHinhAnhList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}




