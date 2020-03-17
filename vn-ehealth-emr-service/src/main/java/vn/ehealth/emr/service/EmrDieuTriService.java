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

import vn.ehealth.emr.model.EmrDieuTri;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrDieuTriRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrDieuTriService {
    
    @Autowired 
    private EmrDieuTriRepository emrDieuTriRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrDieuTri> getById(ObjectId id) {
        return emrDieuTriRepository.findById(id);
    }
    
    public List<EmrDieuTri> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrDieuTriRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);        
    }
    
    public List<EmrDieuTri> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrDieuTriRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);        
    }

    public EmrDieuTri save(@Nonnull EmrDieuTri dieutri, ObjectId userId, String jsonSt) {
        if(dieutri.id == null && dieutri.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(dieutri.emrHoSoBenhAnId).orElseThrow();
            dieutri.emrBenhNhanId = hsba.emrBenhNhanId;
            dieutri.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            
            if(hsba.emrVaoKhoas != null && dieutri.emrKhoaDieuTri != null) {
                var maKhoaDieuTri = dieutri.emrKhoaDieuTri.emrDmKhoaDieuTri.ma;
                
                var vaokhoa = hsba.emrVaoKhoas.stream()
                                .filter(x -> x.emrDmKhoaDieuTri.ma == maKhoaDieuTri)
                                .findFirst();
                
                vaokhoa.ifPresent(x -> dieutri.emrKhoaDieuTri = x);
            }
        }
        if (dieutri.id == null) {
        	emrLogService.logAction(EmrDieuTri.class.getName(), dieutri.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(dieutri), jsonSt);
        } else {
        	emrLogService.logAction(EmrDieuTri.class.getName(), dieutri.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(dieutri), jsonSt);
        }
        return emrDieuTriRepository.save(dieutri);        
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var dieutri = emrDieuTriRepository.findById(id);
        dieutri.ifPresent(x -> {            
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrDieuTriRepository.save(x);
            emrLogService.logAction(EmrDieuTri.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
        
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrDieuTri> dtList, @Nonnull List<Object> dtObjList, String jsonSt) {
        for(int i = 0; i < dtList.size(); i++) {
            var dt = dtList.get(i);
            if(dt.idhis != null) {
            	dt.id = emrDieuTriRepository.findByIdhis(dt.idhis).map(x -> x.id).orElse(null);
            }
            var check = dt.id;
            @SuppressWarnings("unchecked")
			var  dtObject= (Map<String, Object>) dtObjList.get(i);
            @SuppressWarnings("unchecked")
			Map<String, Object> emrDmKhoaDieuTri = (Map<String, Object>) dtObject.get("emrDmKhoaDieuTri");
            if (emrDmKhoaDieuTri != null && hsba.emrVaoKhoas != null) {
            	var maKhoaDieuTri =  (String) emrDmKhoaDieuTri.get("ma");
            	
            	dt.emrKhoaDieuTri = hsba.emrVaoKhoas.stream()
                      .filter(x -> x.emrDmKhoaDieuTri.ma.equals(maKhoaDieuTri))
                      .findFirst()
                      .orElse(null);
            }
            dt.emrHoSoBenhAnId = hsba.id;
            dt.emrBenhNhanId = hsba.emrBenhNhanId;
            dt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            dt = emrDieuTriRepository.save(dt);
            dtList.set(i, dt);
            if(check == null) {
                emrLogService.logAction(EmrDieuTri.class.getName(), dt.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(dt), "");
            } else {
            	emrLogService.logAction(EmrDieuTri.class.getName(), dt.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(dt), "");
            } 
        }         
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrDieuTriList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrDieuTri.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrDieuTri.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrDieuTri.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var dieutri = JsonUtil.parseObject(log.noiDung, EmrDieuTri.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("dieutri", dieutri, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
