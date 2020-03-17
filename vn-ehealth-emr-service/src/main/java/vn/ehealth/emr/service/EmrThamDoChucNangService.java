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
import vn.ehealth.emr.model.EmrThamDoChucNang;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.repository.EmrThamDoChucNangRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrThamDoChucNangService {

    @Autowired
    private EmrThamDoChucNangRepository emrThamDoChucNangRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public List<EmrThamDoChucNang> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrThamDoChucNangRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrThamDoChucNang> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrThamDoChucNangRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrThamDoChucNang save(EmrThamDoChucNang tdcn, ObjectId userId, String jsonSt) {
        if(tdcn.id == null && tdcn.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(tdcn.emrHoSoBenhAnId).orElseThrow();
            tdcn.emrBenhNhanId = hsba.emrBenhNhanId;
            tdcn.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (tdcn.id == null) {
        	emrLogService.logAction(EmrThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(tdcn), jsonSt);
        } else {
        	emrLogService.logAction(EmrThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(tdcn), jsonSt);
        }
        return emrThamDoChucNangRepository.save(tdcn);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var tdcn = emrThamDoChucNangRepository.findById(id);
        tdcn.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrThamDoChucNangRepository.save(x);
            emrLogService.logAction(EmrThamDoChucNang.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrThamDoChucNang> tdcnList, String jsonSt) {
        for(int i = 0; i < tdcnList.size(); i++) {
            var tdcn = tdcnList.get(i);
            if(tdcn.idhis != null) {
            	tdcn.id = emrThamDoChucNangRepository.findByIdhis(tdcn.idhis).map(x -> x.id).orElse(null);
            }
            var check = tdcn.id;
            tdcn.emrHoSoBenhAnId = hsba.id;
            tdcn.emrBenhNhanId = hsba.emrBenhNhanId;
            tdcn.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            tdcn = emrThamDoChucNangRepository.save(tdcn);
            tdcnList.set(i, tdcn);
            if(check == null) {
                emrLogService.logAction(EmrThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(tdcn), "");
            } else {
            	emrLogService.logAction(EmrThamDoChucNang.class.getName(), tdcn.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(tdcn), "");
            } 
        }
        
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrThamDoChucNangList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrThamDoChucNang.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrThamDoChucNang.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrThamDoChucNang.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var tdcn = JsonUtil.parseObject(log.noiDung, EmrThamDoChucNang.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("tdcn", tdcn, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
