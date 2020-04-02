package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.PhauThuatThuThuat;
import vn.ehealth.cdr.repository.PhauThuatThuThuatRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class PhauThuatThuThuatService {

    @Autowired private PhauThuatThuThuatRepository phauThuatThuThuatRepository;
    
    @Autowired private LogService logService;
    
    public List<PhauThuatThuThuat> getByHoSoBenhAnId(ObjectId hoSoBenhAnId){
        return phauThuatThuThuatRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
   
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<PhauThuatThuThuat> ptttList, String jsonSt) {
        for(int i = 0; i < ptttList.size(); i++) {
            var pttt = ptttList.get(i);
            if(pttt.idhis != null) {
            	pttt.id = phauThuatThuThuatRepository.findByIdhis(pttt.idhis).map(x -> x.id).orElse(null);
            }
            var check = pttt.id;
            pttt.hoSoBenhAnId = hsba.id;
            pttt.benhNhanId = hsba.benhNhanId;
            pttt.coSoKhamBenhId = hsba.coSoKhamBenhId;
            pttt = phauThuatThuThuatRepository.save(pttt);
            ptttList.set(i, pttt);
            if(check == null) {
                logService.logAction(PhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(pttt), "");
            } else {
            	logService.logAction(PhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(pttt), "");
            } 
        }
        
        logService.logAction(HoSoBenhAn.class.getName() + ".PhauThuatThuThuatList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt); 
    }    
}
