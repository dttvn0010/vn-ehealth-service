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
import vn.ehealth.emr.model.EmrXetNghiem;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.repository.EmrXetNghiemRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrXetNghiemService {

    @Autowired 
    private EmrXetNghiemRepository emrXetNghiemRepository;
    
    @Autowired EmrLogService emrLogService;
    
	private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public List<EmrXetNghiem> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrXetNghiemRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrXetNghiem> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrXetNghiemRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrXetNghiem save(@Nonnull EmrXetNghiem xn, ObjectId userId, String jsonSt) {
        if(xn.id == null && xn.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(xn.emrHoSoBenhAnId).orElseThrow();
            xn.emrBenhNhanId = hsba.emrBenhNhanId;
            xn.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (xn.id == null) {
        	emrLogService.logAction(EmrXetNghiem.class.getName(), xn.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(xn), jsonSt);
        } else {
        	emrLogService.logAction(EmrXetNghiem.class.getName(), xn.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(xn), jsonSt);
        }
        return emrXetNghiemRepository.save(xn);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrXetNghiem> xetnghiemList, String jsonSt) {
        for(int i = 0; i < xetnghiemList.size(); i++) {
            var xetnghiem = xetnghiemList.get(i);
            if(xetnghiem.idhis != null) {
                xetnghiem.id = emrXetNghiemRepository.findByIdhis(xetnghiem.idhis).map(x -> x.id).orElse(null);
            }
            var check = xetnghiem.id;
            xetnghiem.emrHoSoBenhAnId = hsba.id;
            xetnghiem.emrBenhNhanId = hsba.emrBenhNhanId;
            xetnghiem.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            xetnghiem = emrXetNghiemRepository.save(xetnghiem);
            xetnghiemList.set(i, xetnghiem);
            if(check == null) {
                emrLogService.logAction(EmrXetNghiem.class.getName(), xetnghiem.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(xetnghiem), "");
            } else {
            	emrLogService.logAction(EmrXetNghiem.class.getName(), xetnghiem.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(xetnghiem), "");
            }
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrXetNghiemList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var xn = emrXetNghiemRepository.findById(id);
        xn.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrXetNghiemRepository.save(x);
            emrLogService.logAction(EmrXetNghiem.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrXetNghiem.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrXetNghiem.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrXetNghiem.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var xetnghiem = JsonUtil.parseObject(log.noiDung, EmrXetNghiem.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("xetnghiem", xetnghiem, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
