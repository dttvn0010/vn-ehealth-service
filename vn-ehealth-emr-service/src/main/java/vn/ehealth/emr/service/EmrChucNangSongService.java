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

import vn.ehealth.emr.model.EmrChucNangSong;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrChucNangSongRepository;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;

@Service
public class EmrChucNangSongService {

    @Autowired 
    private EmrChucNangSongRepository emrChucNangSongRepository;
    
    @Autowired EmrLogService emrLogService;
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
    
    @Autowired UserService userService;
    
    @Autowired
    private EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    public Optional<EmrChucNangSong> getById(ObjectId id) {
        return emrChucNangSongRepository.findById(id);
    }
    
    public List<EmrChucNangSong> getByEmrHoSoBenhAnId(ObjectId emrHoSoBenhAnId) {
        return emrChucNangSongRepository.findByEmrHoSoBenhAnIdAndTrangThai(emrHoSoBenhAnId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public List<EmrChucNangSong> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrChucNangSongRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public EmrChucNangSong save(@Nonnull EmrChucNangSong cns, ObjectId userId, String jsonSt) {
        if(cns.id == null && cns.emrHoSoBenhAnId == null) {
            var hsba = emrHoSoBenhAnRepository.findById(cns.emrHoSoBenhAnId).orElseThrow();
            cns.emrBenhNhanId = hsba.emrBenhNhanId;
            cns.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            
            if(hsba.emrVaoKhoas != null && cns.emrKhoaDieuTri != null) {
                var maKhoaDieuTri = cns.emrKhoaDieuTri.emrDmKhoaDieuTri.ma;
                
                var vaokhoa = hsba.emrVaoKhoas.stream()
                                .filter(x -> x.emrDmKhoaDieuTri.ma == maKhoaDieuTri)
                                .findFirst();
                
                vaokhoa.ifPresent(x -> cns.emrKhoaDieuTri = x);
            }
        }
        if (cns.id == null) {
        	emrLogService.logAction(EmrChucNangSong.class.getName(), cns.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                    JsonUtil.dumpObject(cns), jsonSt);
        } else {
        	emrLogService.logAction(EmrChucNangSong.class.getName(), cns.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                    JsonUtil.dumpObject(cns), jsonSt);
        }
        return emrChucNangSongRepository.save(cns);
    }
    
    public void delete(ObjectId id, ObjectId userId) {
        var cns = emrChucNangSongRepository.findById(id);
        cns.ifPresent(x -> {
            x.trangThai = TRANGTHAI_DULIEU.DA_XOA;
            emrChucNangSongRepository.save(x);
            emrLogService.logAction(EmrChucNangSong.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
        });
    }
    
    public void createOrUpdateFromHIS(ObjectId userId, @Nonnull EmrHoSoBenhAn hsba, @Nonnull List<EmrChucNangSong> cnsList, @Nonnull List<Object> cnsObjList, String jsonSt) {
        for(int i = 0; i < cnsList.size(); i++) {
            var cns = cnsList.get(i);
            if(cns.idhis != null) {
            	cns.id = emrChucNangSongRepository.findByIdhis(cns.idhis).map(x -> x.id).orElse(null);
            }
            var check = cns.id;
            @SuppressWarnings("unchecked")
			var  cnsObject= (Map<String, Object>) cnsObjList.get(i);
            @SuppressWarnings("unchecked")
			Map<String, Object> emrDmKhoaDieuTri = (Map<String, Object>) cnsObject.get("emrDmKhoaDieuTri");
            if (emrDmKhoaDieuTri != null && hsba.emrVaoKhoas != null) {
            	var maKhoaDieuTri =  (String) emrDmKhoaDieuTri.get("ma");
            	
            	cns.emrKhoaDieuTri = hsba.emrVaoKhoas.stream()
                      .filter(x -> x.emrDmKhoaDieuTri.ma.equals(maKhoaDieuTri))
                      .findFirst()
                      .orElse(null);
            }
            
            cns.emrHoSoBenhAnId = hsba.id;
            cns.emrBenhNhanId = hsba.emrBenhNhanId;
            cns.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            cns = emrChucNangSongRepository.save(cns);
            cnsList.set(i, cns);
            if(check == null) {
                emrLogService.logAction(EmrChucNangSong.class.getName(), cns.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                		JsonUtil.dumpObject(cns), "");
            } else {
            	emrLogService.logAction(EmrChucNangSong.class.getName(), cns.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                        JsonUtil.dumpObject(cns), "");
            } 
        }
        emrLogService.logAction(EmrHoSoBenhAn.class.getName() + ".EmrChucNangSongList", hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                "", jsonSt);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrChucNangSong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrChucNangSong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrChucNangSong.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var cns = JsonUtil.parseObject(log.noiDung, EmrChucNangSong.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("cns", cns, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
}
