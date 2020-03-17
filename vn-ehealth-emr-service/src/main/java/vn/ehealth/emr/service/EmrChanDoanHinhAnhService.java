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

import vn.ehealth.emr.model.EmrChanDoanHinhAnh;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrChanDoanHinhAnhRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrChanDoanHinhAnhService {
	
	private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");

    @Autowired 
    private EmrChanDoanHinhAnhRepository emrChanDoanHinhAnhRepository;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    @Autowired EmrLogService emrLogService;
    
    @Autowired UserService userService;
    
    public List<EmrChanDoanHinhAnh> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrChanDoanHinhAnhRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrChanDoanHinhAnh> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrChanDoanHinhAnhRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrChanDoanHinhAnh save(EmrChanDoanHinhAnh cdha, ObjectId userId, String jsonSt) {
        if(cdha.id == null && cdha.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(cdha.emrHoSoBenhAnId).orElseThrow();
            cdha.emrBenhNhanId = hsba.emrBenhNhanId;
            cdha.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (cdha.id == null) {
        	emrLogService.logAction(EmrChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(cdha), jsonSt);
        } else {
        	emrLogService.logAction(EmrChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(cdha), jsonSt);
        }
        
        return emrChanDoanHinhAnhRepository.save(cdha);
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrChanDoanHinhAnh> cdhaList, String jsonSt) {
        for(int i = 0; i < cdhaList.size(); i++) {
            var cdha = cdhaList.get(i);
            if(cdha.idhis != null) {
                cdha.id = emrChanDoanHinhAnhRepository.findByIdhis(cdha.idhis).map(x -> x.id).orElse(null);
            }
            var check = cdha.id;
            cdha.emrHoSoBenhAnId = hsba.id;
            cdha.emrBenhNhanId = hsba.emrBenhNhanId;
            cdha.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            cdha = emrChanDoanHinhAnhRepository.save(cdha);
            cdhaList.set(i, cdha);
            if(check == null) {
                emrLogService.logAction(EmrChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(cdha), "");
            } else {
            	emrLogService.logAction(EmrChanDoanHinhAnh.class.getName(), cdha.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(cdha), "");
            }
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrChanDoanHinhAnhList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var cdha = emrChanDoanHinhAnhRepository.findById(id);
        cdha.ifPresent(x -> {
        	emrLogService.logAction(EmrChanDoanHinhAnh.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrChanDoanHinhAnhRepository.save(x);
        });
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrChanDoanHinhAnh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrChanDoanHinhAnh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrChanDoanHinhAnh.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var cdha = JsonUtil.parseObject(log.noiDung, EmrChanDoanHinhAnh.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("cdha", cdha, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}




