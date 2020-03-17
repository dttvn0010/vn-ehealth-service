package vn.ehealth.emr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrHinhAnhTonThuong;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrHinhAnhTonThuongRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrHinhAnhTonThuongService {

    @Autowired 
    private EmrHinhAnhTonThuongRepository emrHinhAnhTonThuongRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrHinhAnhTonThuong> getById(ObjectId id) {
        return emrHinhAnhTonThuongRepository.findById(id);
    }
    
    public List<EmrHinhAnhTonThuong> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrHinhAnhTonThuongRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrHinhAnhTonThuong> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrHinhAnhTonThuongRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrHinhAnhTonThuong save(EmrHinhAnhTonThuong hatt, ObjectId userId, String jsonSt) {
        if(hatt.id == null && hatt.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(hatt.emrHoSoBenhAnId).orElseThrow();
            hatt.emrBenhNhanId = hsba.emrBenhNhanId;
            hatt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }    
        if (hatt.id == null) {
        	emrLogService.logAction(EmrHinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(hatt), jsonSt);
        } else {
        	emrLogService.logAction(EmrHinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(hatt), jsonSt);
        }
        
        return emrHinhAnhTonThuongRepository.save(hatt);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var hatt = emrHinhAnhTonThuongRepository.findById(id);
        hatt.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrHinhAnhTonThuongRepository.save(x);
            emrLogService.logAction(EmrHinhAnhTonThuong.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }  
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrHinhAnhTonThuong> hattList, String jsonSt) {
        for(int i = 0; i < hattList.size(); i++) {
            var hatt = hattList.get(i);
            if(hatt.idhis != null) {
            	hatt.id = emrHinhAnhTonThuongRepository.findByIdhis(hatt.idhis).map(x -> x.id).orElse(null);
            }
            var check = hatt.id;
            hatt.emrHoSoBenhAnId = hsba.id;
            hatt.emrBenhNhanId = hsba.emrBenhNhanId;
            hatt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            hatt = emrHinhAnhTonThuongRepository.save(hatt);
            hattList.set(i, hatt);
            if(check == null) {
                emrLogService.logAction(EmrHinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(hatt), "");
            } else {
            	emrLogService.logAction(EmrHinhAnhTonThuong.class.getName(), hatt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(hatt), "");
            } 
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrHinhAnhTonThuongList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrHinhAnhTonThuong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrHinhAnhTonThuong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrHinhAnhTonThuong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var hatt = JsonUtil.parseObject(log.noiDung, EmrHinhAnhTonThuong.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("hatt", hatt, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
