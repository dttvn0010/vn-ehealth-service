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

import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.model.EmrHoiChan;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.repository.EmrHoiChanRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrHoiChanService {

    @Autowired
    private EmrHoiChanRepository emrHoiChanRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrHoiChan> getById(ObjectId id) {
        return emrHoiChanRepository.findById(id);
    }
    
    public List<EmrHoiChan> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrHoiChanRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrHoiChan> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrHoiChanRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrHoiChan save(@Nonnull EmrHoiChan hoichan, ObjectId userId, String jsonSt) {
        if(hoichan.id == null && hoichan.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(hoichan.emrHoSoBenhAnId).orElseThrow();
            hoichan.emrBenhNhanId = hsba.emrBenhNhanId;
            hoichan.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            
            if(hsba.emrVaoKhoas != null && hoichan.emrKhoaDieuTri != null) {
                var maKhoaDieuTri = hoichan.emrKhoaDieuTri.emrDmKhoaDieuTri.ma;
                
                var vaokhoa = hsba.emrVaoKhoas.stream()
                                .filter(x -> x.emrDmKhoaDieuTri.ma == maKhoaDieuTri)
                                .findFirst();
                
                vaokhoa.ifPresent(x -> hoichan.emrKhoaDieuTri = x);
            }
        }
        if (hoichan.id == null) {
        	emrLogService.logAction(EmrHoiChan.class.getName(), hoichan.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(hoichan), jsonSt);
        } else {
        	emrLogService.logAction(EmrHoiChan.class.getName(), hoichan.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(hoichan), jsonSt);
        }
        
        return emrHoiChanRepository.save(hoichan);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var hoichan = emrHoiChanRepository.findById(id);
        hoichan.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrHoiChanRepository.save(x);
            emrLogService.logAction(EmrHoiChan.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrHoiChan> hcList, @Nonnull List<Object> hcObjList, String jsonSt) {
        for(int i = 0; i < hcList.size(); i++) {
            var hc = hcList.get(i);
            if(hc.idhis != null) {
            	hc.id = emrHoiChanRepository.findByIdhis(hc.idhis).map(x -> x.id).orElse(null);
            }
            var check = hc.id;
            @SuppressWarnings("unchecked")
			var  hcObject= (Map<String, Object>) hcObjList.get(i);
            @SuppressWarnings("unchecked")
			Map<String, Object> emrDmKhoaDieuTri = (Map<String, Object>) hcObject.get("emrDmKhoaDieuTri");
            if (emrDmKhoaDieuTri != null && hsba.emrVaoKhoas != null) {
            	var maKhoaDieuTri =  (String) emrDmKhoaDieuTri.get("ma");
            	
            	hc.emrKhoaDieuTri = hsba.emrVaoKhoas.stream()
                      .filter(x -> x.emrDmKhoaDieuTri.ma.equals(maKhoaDieuTri))
                      .findFirst()
                      .orElse(null);
            }
            hc.emrHoSoBenhAnId = hsba.id;
            hc.emrBenhNhanId = hsba.emrBenhNhanId;
            hc.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            hc = emrHoiChanRepository.save(hc);
            hcList.set(i, hc);
            if(check == null) {
                emrLogService.logAction(EmrHoiChan.class.getName(), hc.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(hc), "");
            } else {
            	emrLogService.logAction(EmrHoiChan.class.getName(), hc.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(hc), "");
            } 
        }
        
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrHoiChanList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);        
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrHoiChan.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrHoiChan.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrHoiChan.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var hoichan = JsonUtil.parseObject(log.noiDung, EmrHoiChan.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("hoichan", hoichan, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
