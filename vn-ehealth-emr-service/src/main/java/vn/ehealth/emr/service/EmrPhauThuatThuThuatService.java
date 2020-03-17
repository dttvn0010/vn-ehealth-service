package vn.ehealth.emr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.model.EmrPhauThuatThuThuat;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.repository.EmrPhauThuatThuThuatRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrPhauThuatThuThuatService {

    @Autowired 
    private EmrPhauThuatThuThuatRepository emrPhauThuatThuThuatRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public List<EmrPhauThuatThuThuat> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId){
        return emrPhauThuatThuThuatRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrPhauThuatThuThuat> getByEmrBenhNhanId(ObjectId emrBenhNhanId){
        return emrPhauThuatThuThuatRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrPhauThuatThuThuat save(@Nonnull EmrPhauThuatThuThuat pttt, ObjectId userId, String jsonSt) {
        if(pttt.id == null && pttt.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(pttt.emrHoSoBenhAnId).orElseThrow();
            pttt.emrHoSoBenhAnId = hsba.id;
            pttt.emrBenhNhanId = hsba.emrBenhNhanId;
            pttt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        
        if (pttt.id == null) {
        	emrLogService.logAction(EmrPhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(pttt), jsonSt);
        } else {
        	emrLogService.logAction(EmrPhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(pttt), jsonSt);
        }
        
        return emrPhauThuatThuThuatRepository.save(pttt);                
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrPhauThuatThuThuat> ptttList, String jsonSt) {
        for(int i = 0; i < ptttList.size(); i++) {
            var pttt = ptttList.get(i);
            if(pttt.idhis != null) {
            	pttt.id = emrPhauThuatThuThuatRepository.findByIdhis(pttt.idhis).map(x -> x.id).orElse(null);
            }
            var check = pttt.id;
            pttt.emrHoSoBenhAnId = hsba.id;
            pttt.emrBenhNhanId = hsba.emrBenhNhanId;
            pttt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            pttt = emrPhauThuatThuThuatRepository.save(pttt);
            ptttList.set(i, pttt);
            if(check == null) {
                emrLogService.logAction(EmrPhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(pttt), "");
            } else {
            	emrLogService.logAction(EmrPhauThuatThuThuat.class.getName(), pttt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(pttt), "");
            } 
        }
        
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrPhauThuatThuThuatList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt); 
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var pttt = emrPhauThuatThuThuatRepository.findById(id);
        pttt.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrPhauThuatThuThuatRepository.save(x);
            emrLogService.logAction(EmrPhauThuatThuThuat.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrPhauThuatThuThuat.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrPhauThuatThuThuat.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrPhauThuatThuThuat.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var pttt = JsonUtil.parseObject(log.noiDung, EmrPhauThuatThuThuat.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("pttt", pttt, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
