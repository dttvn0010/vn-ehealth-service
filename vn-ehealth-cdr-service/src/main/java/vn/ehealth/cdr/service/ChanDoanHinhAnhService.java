package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.ChanDoanHinhAnh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.ChanDoanHinhAnhRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;

@Service
public class ChanDoanHinhAnhService {
	
	@Autowired 
    private ChanDoanHinhAnhRepository chanDoanHinhAnhRepository;
    
    @Autowired private LogService logService;
    
    public List<ChanDoanHinhAnh> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return chanDoanHinhAnhRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(@Nonnull HoSoBenhAn hsba, @Nonnull List<ChanDoanHinhAnh> cdhaList, String jsonSt) {
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
                logService.logAction(ChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.TAO_MOI, new Date(), null, 
                		JsonUtil.dumpObject(cdha), "");
            } else {
            	logService.logAction(ChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.CHINH_SUA, new Date(), null, 
                        JsonUtil.dumpObject(cdha), "");
            }
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".ChanDoanHinhAnhList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), null, 
                "", jsonSt);
    }
}




