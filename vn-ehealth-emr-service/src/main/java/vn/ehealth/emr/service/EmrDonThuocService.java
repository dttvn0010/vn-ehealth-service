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

import vn.ehealth.emr.model.EmrDonThuoc;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrDonThuocRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrDonThuocService {

    @Autowired 
    private EmrDonThuocRepository emrDonThuocRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrDonThuoc> getById(ObjectId id) {
        return emrDonThuocRepository.findById(id);
    }
    
    public List<EmrDonThuoc> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrDonThuocRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrDonThuoc> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrDonThuocRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrDonThuoc save(@Nonnull EmrDonThuoc donthuoc, ObjectId userId, String jsonSt) {
        if(donthuoc.id == null && donthuoc.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(donthuoc.emrHoSoBenhAnId).orElseThrow();
            donthuoc.emrBenhNhanId = hsba.emrBenhNhanId;
            donthuoc.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (donthuoc.id == null) {
        	emrLogService.logAction(EmrDonThuoc.class.getName(), donthuoc.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(donthuoc), jsonSt);
        } else {
        	emrLogService.logAction(EmrDonThuoc.class.getName(), donthuoc.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(donthuoc), jsonSt);
        }
        return emrDonThuocRepository.save(donthuoc);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var donthuoc = emrDonThuocRepository.findById(id);
        donthuoc.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrDonThuocRepository.save(x);
            emrLogService.logAction(EmrDonThuoc.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrDonThuoc> dtList, String jsonSt) {
        for(int i = 0; i < dtList.size(); i++) {
            var dt = dtList.get(i);
            if(dt.idhis != null) {
                dt.id = emrDonThuocRepository.findByIdhis(dt.idhis).map(x -> x.id).orElse(null);
            }
            var check = dt.id;
            dt.emrHoSoBenhAnId = hsba.id;
            dt.emrBenhNhanId = hsba.emrBenhNhanId;
            dt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            dt = emrDonThuocRepository.save(dt);
            dtList.set(i, dt);
            if(check == null) {
                emrLogService.logAction(EmrDonThuoc.class.getName(), dt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(dt), "");
            } else {
            	emrLogService.logAction(EmrDonThuoc.class.getName(), dt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(dt), "");
            } 
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrDonThuocList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrDonThuoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrDonThuoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrDonThuoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var donthuoc = JsonUtil.parseObject(log.noiDung, EmrDonThuoc.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("donthuoc", donthuoc, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
