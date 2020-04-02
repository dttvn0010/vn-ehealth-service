package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.auth.service.UserService;
import vn.ehealth.cdr.model.GiaiPhauBenh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.repository.GiaiPhauBenhRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class GiaiPhauBenhService {

    @Autowired 
    private GiaiPhauBenhRepository giaiPhauBenhRepository;
    
    @Autowired LogService logService;
    
    @Autowired UserService userService;
    
    public List<GiaiPhauBenh> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return giaiPhauBenhRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
        
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<GiaiPhauBenh> gpbList, String jsonSt) {
        for(int i = 0; i < gpbList.size(); i++) {
            var gpb = gpbList.get(i);
            if(gpb.idhis != null) {
            	gpb.id = giaiPhauBenhRepository.findByIdhis(gpb.idhis).map(x -> x.id).orElse(null);
            }
            var check = gpb.id;
            
            gpb.hoSoBenhAnId = hsba.id;
            gpb.benhNhanId = hsba.benhNhanId;
            gpb.coSoKhamBenhId = hsba.coSoKhamBenhId;
            gpb = giaiPhauBenhRepository.save(gpb);
            gpbList.set(i, gpb);
            if(check == null) {
                logService.logAction(GiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(gpb), "");
            } else {
            	logService.logAction(GiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(gpb), "");
            } 
        }
        logService.logAction(HoSoBenhAn.class.getName() + ".GiaiPhauBenhList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
}
