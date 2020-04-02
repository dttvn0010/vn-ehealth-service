package vn.ehealth.cdr.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.ThamDoChucNang;
import vn.ehealth.cdr.repository.ThamDoChucNangRepository;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.MA_HANH_DONG;
import vn.ehealth.hl7.fhir.core.util.Constants.TRANGTHAI_DULIEU;

@Service
public class ThamDoChucNangService {

    @Autowired private ThamDoChucNangRepository thamDoChucNangRepository;
    
    @Autowired private LogService logService;
    
    public List<ThamDoChucNang> getByHoSoBenhAnId(ObjectId hoSoBenhAnId) {
        return thamDoChucNangRepository.findByHoSoBenhAnIdAndTrangThai(hoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull HoSoBenhAn hsba, @Nonnull List<ThamDoChucNang> tdcnList, String jsonSt) {
        for(int i = 0; i < tdcnList.size(); i++) {
            var tdcn = tdcnList.get(i);
            if(tdcn.idhis != null) {
            	tdcn.id = thamDoChucNangRepository.findByIdhis(tdcn.idhis).map(x -> x.id).orElse(null);
            }
            var check = tdcn.id;
            tdcn.hoSoBenhAnId = hsba.id;
            tdcn.benhNhanId = hsba.benhNhanId;
            tdcn.coSoKhamBenhId = hsba.coSoKhamBenhId;
            tdcn = thamDoChucNangRepository.save(tdcn);
            tdcnList.set(i, tdcn);
            if(check == null) {
                logService.logAction(ThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(tdcn), "");
            } else {
            	logService.logAction(ThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(tdcn), "");
            } 
        }
        
        logService.logAction(HoSoBenhAn.class.getName() + ".ThamDoChucNangList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
}
