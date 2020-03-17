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

import vn.ehealth.emr.model.EmrGiaiPhauBenh;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrGiaiPhauBenhRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrGiaiPhauBenhService {

    @Autowired 
    private EmrGiaiPhauBenhRepository emrGiaiPhauBenhRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public List<EmrGiaiPhauBenh> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrGiaiPhauBenhRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrGiaiPhauBenh> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrGiaiPhauBenhRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrGiaiPhauBenh save(EmrGiaiPhauBenh gpb, ObjectId userId, String jsonSt) {
        if(gpb.id == null && gpb.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(gpb.emrHoSoBenhAnId).orElseThrow();
            gpb.emrBenhNhanId = hsba.emrBenhNhanId;
            gpb.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (gpb.id == null) {
        	emrLogService.logAction(EmrGiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(gpb), jsonSt);
        } else {
        	emrLogService.logAction(EmrGiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(gpb), jsonSt);
        }
        
        return emrGiaiPhauBenhRepository.save(gpb);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var gpb = emrGiaiPhauBenhRepository.findById(id);
        gpb.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrGiaiPhauBenhRepository.save(x);
            emrLogService.logAction(EmrGiaiPhauBenh.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrGiaiPhauBenh> gpbList, String jsonSt) {
        for(int i = 0; i < gpbList.size(); i++) {
            var gpb = gpbList.get(i);
            if(gpb.idhis != null) {
            	gpb.id = emrGiaiPhauBenhRepository.findByIdhis(gpb.idhis).map(x -> x.id).orElse(null);
            }
            var check = gpb.id;
            
            gpb.emrHoSoBenhAnId = hsba.id;
            gpb.emrBenhNhanId = hsba.emrBenhNhanId;
            gpb.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            gpb = emrGiaiPhauBenhRepository.save(gpb);
            gpbList.set(i, gpb);
            if(check == null) {
                emrLogService.logAction(EmrGiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(gpb), "");
            } else {
            	emrLogService.logAction(EmrGiaiPhauBenh.class.getName(), gpb.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(gpb), "");
            } 
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrGiaiPhauBenhList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrGiaiPhauBenh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrGiaiPhauBenh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrGiaiPhauBenh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var gpb = JsonUtil.parseObject(log.noiDung, EmrGiaiPhauBenh.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("gpb", gpb, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
