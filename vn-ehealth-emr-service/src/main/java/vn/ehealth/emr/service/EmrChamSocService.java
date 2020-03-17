package vn.ehealth.emr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import vn.ehealth.emr.model.EmrChamSoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrChamSocRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrChamSocService {

    @Autowired
    private EmrChamSocRepository emrChamSocRepository;
    
    @Autowired EmrLogService emrLogService;
    
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrChamSoc> getById(ObjectId id) {
        return emrChamSocRepository.findById(id);
    }
    
    public List<EmrChamSoc> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrChamSocRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrChamSoc> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrChamSocRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrChamSoc save(@Nonnull EmrChamSoc chamsoc, ObjectId userId, String jsonSt) {
        if(chamsoc.id == null && chamsoc.emrHoSoBenhAnId != null) {
            var hsba = emrHoSoBenhAnRepository.findById(chamsoc.emrHoSoBenhAnId).orElseThrow();
            chamsoc.emrBenhNhanId = hsba.emrBenhNhanId;
            chamsoc.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
        }
        if (chamsoc.id == null) {
        	emrLogService.logAction(EmrChamSoc.class.getName(), chamsoc.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(chamsoc), jsonSt);
        } else {
        	emrLogService.logAction(EmrChamSoc.class.getName(), chamsoc.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(chamsoc), jsonSt);
        }
        return emrChamSocRepository.save(chamsoc);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var emrChamSoc = emrChamSocRepository.findById(id);
        emrChamSoc.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrChamSocRepository.save(x);
            emrLogService.logAction(EmrChamSoc.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrChamSoc> csList, List<Object> csObjList, String jsonSt) {
        for(int i = 0; i < csList.size(); i++) {
            var cs = csList.get(i);
            if(cs.idhis != null) {
                cs.id = emrChamSocRepository.findByIdhis(cs.idhis).map(x -> x.id).orElse(null);
            }
            var check = cs.id;
            @SuppressWarnings("unchecked")
			var  csObject= (Map<String, Object>) csObjList.get(i);
            @SuppressWarnings("unchecked")
			Map<String, Object> emrDmKhoaDieuTri = (Map<String, Object>) csObject.get("emrDmKhoaDieuTri");
            if (emrDmKhoaDieuTri != null && hsba.emrVaoKhoas != null) {
            	var maKhoaDieuTri =  (String) emrDmKhoaDieuTri.get("ma");
            	cs.emrKhoaDieuTri = hsba.emrVaoKhoas.stream()
                      .filter(x -> x.emrDmKhoaDieuTri.ma.equals(maKhoaDieuTri))
                      .findFirst()
                      .orElse(null);
            }
            cs.emrHoSoBenhAnId = hsba.id;
            cs.emrBenhNhanId = hsba.emrBenhNhanId;
            cs.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            cs = emrChamSocRepository.save(cs);
            csList.set(i, cs);
            if(check == null) {
                emrLogService.logAction(EmrChamSoc.class.getName(), cs.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(cs), "");
            } else {
            	emrLogService.logAction(EmrChamSoc.class.getName(), cs.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(cs), "");
            }  
        }      
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrChamSocList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrChamSoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrChamSoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrChamSoc.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var chamsoc = JsonUtil.parseObject(log.noiDung, EmrChamSoc.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("chamsoc", chamsoc, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
