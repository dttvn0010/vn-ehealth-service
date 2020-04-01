package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.auth.service.UserService;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.XetNghiem;
import vn.ehealth.cdr.repository.XetNghiemRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class XetNghiemService {

    @Autowired 
    private XetNghiemRepository xetNghiemRepository;
    
    @Autowired LogService logService;
    
	@Autowired UserService userService;
    
    public List<XetNghiem> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return xetNghiemRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<XetNghiem> xetnghiemList, String jsonSt) {
        for(int i = 0; i < xetnghiemList.size(); i++) {
            var xetnghiem = xetnghiemList.get(i);
            if(xetnghiem.idhis != null) {
                xetnghiem.id = xetNghiemRepository.findByIdhis(xetnghiem.idhis).map(x -> x.id).orElse(null);
            }
            var check = xetnghiem.id;
            xetnghiem.hoSoBenhAnId = hsba.id;
            xetnghiem.benhNhanId = hsba.benhNhanId;
            xetnghiem.coSoKhamBenhId = hsba.coSoKhamBenhId;
            xetnghiem = xetNghiemRepository.save(xetnghiem);
            xetnghiemList.set(i, xetnghiem);
            if(check == null) {
                logService.logAction(XetNghiem.class.getName(), xetnghiem.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(xetnghiem), "");
            } else {
            	logService.logAction(XetNghiem.class.getName(), xetnghiem.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(xetnghiem), "");
            }
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".EmrXetNghiemList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}
