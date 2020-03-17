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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.ehealth.emr.model.EmrFileDinhKem;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.repository.EmrHoSoBenhAnRepository;
import vn.ehealth.emr.utils.Constants.MA_HANH_DONG;
import vn.ehealth.emr.utils.Constants.NGUON_DU_LIEU;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_DULIEU;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_HOSO;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.JsonUtil;

@Service
public class EmrHoSoBenhAnService {
   
    //private Logger logger = LoggerFactory.getLogger(EmrHoSoBenhAnService.class);
    private SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
            
    @Autowired EmrHoSoBenhAnRepository emrHoSoBenhAnRepository;
    
    @Autowired EmrHinhAnhTonThuongService emrHinhAnhTonThuongService;
    @Autowired EmrGiaiPhauBenhService emrGiaiPhauBenhService;
    @Autowired EmrThamDoChucNangService emrThamDoChucNangService;
    @Autowired EmrPhauThuatThuThuatService emrPhauThuatThuThuatService;
    @Autowired EmrChanDoanHinhAnhService emrChanDoanHinhAnhService;
    @Autowired EmrDonThuocService emrDonThuocService;
    @Autowired EmrYhctDonThuocService emrYhctDonThuocService;
    @Autowired EmrXetNghiemService emrXetNghiemService;
    
    @Autowired EmrDmService emrDmService;
    @Autowired EmrBenhNhanService emrBenhNhanService;
    @Autowired EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    @Autowired EmrLogService emrLogService;
    @Autowired UserService userService;
    
    @Autowired MongoTemplate mongoTemplate;
    
    public Optional<EmrHoSoBenhAn> getByMatraodoi(String matraodoi) {
        return emrHoSoBenhAnRepository.findByMatraodoi(matraodoi);
    }
    
    public long countHoSo(ObjectId userId, ObjectId emrCoSoKhamBenhId, int trangThai, String mayte) {
        var query = new Query(Criteria.where("emrCoSoKhamBenhId").is(emrCoSoKhamBenhId)
        								//.orOperator(Criteria.where("dsNguoiXemIds").is(userId), Criteria.where("emrQuanLyNguoiBenh.bacsikhamId").is(userId))
                                        .and("trangThai").is(trangThai)
                                        .and("mayte").regex(mayte)
                             );
        
        return mongoTemplate.count(query, EmrHoSoBenhAn.class);
    }
    
    public List<EmrHoSoBenhAn> getDsHoSo(ObjectId userId, ObjectId emrCoSoKhamBenhId, int trangThai, String mayte, int offset, int limit) {
        var query = new Query(Criteria.where("emrCoSoKhamBenhId").is(emrCoSoKhamBenhId)
        								//.orOperator(Criteria.where("dsNguoiXemIds").is(userId), Criteria.where("emrQuanLyNguoiBenh.bacsikhamId").is(userId))
        								.and("trangThai").is(trangThai)
                                        .and("mayte").regex(mayte)
                             ).skip(offset).limit(limit);
        
        return mongoTemplate.find(query, EmrHoSoBenhAn.class);
    }
    
    public List<EmrHoSoBenhAn> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrHoSoBenhAnRepository.findByEmrBenhNhanIdAndTrangThai(emrBenhNhanId, TRANGTHAI_DULIEU.DEFAULT);
    }
    
    public Optional<EmrHoSoBenhAn> getById(ObjectId id){
        return emrHoSoBenhAnRepository.findById(id);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = emrLogService.getLogs(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return emrLogService.countLogs(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = emrLogService.getLogs(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var hsba = JsonUtil.parseObject(log.noiDung, EmrHoSoBenhAn.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.fullName).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("hsba", hsba, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
    
    public EmrHoSoBenhAn update(@Nonnull EmrHoSoBenhAn hsba, ObjectId userId) {        
        var jsonSt = getHsgoc(hsba.id);
        emrLogService.logAction(EmrHoSoBenhAn.class.getName(), hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                JsonUtil.dumpObject(hsba), jsonSt);
        
        hsba.ngaysua = new Date();
        hsba.nguoisuaId = userId;
        
        return emrHoSoBenhAnRepository.save(hsba);
    }
    
    public EmrHoSoBenhAn createOrUpdateFromHIS(ObjectId userId, @Nonnull ObjectId emrBenhNhanId, @Nonnull ObjectId emrCoSoKhamBenhId, @Nonnull EmrHoSoBenhAn hsba, String jsonSt) {
        hsba.id = emrHoSoBenhAnRepository.findByMatraodoi(hsba.matraodoi).map(x -> x.id).orElse(null);
        boolean createNew = hsba.id == null;
        
        hsba.emrBenhNhanId = emrBenhNhanId;        
        hsba.emrCoSoKhamBenhId = emrCoSoKhamBenhId;
        
        if(createNew) {
            hsba.ngaytao = new Date();
            hsba.nguoitaoId = userId;
        }else {
            hsba.ngaysua = new Date();
            hsba.nguoisuaId = userId;
        }
        
        
        hsba.nguonDuLieu = NGUON_DU_LIEU.TU_HIS;
        hsba.trangThai = TRANGTHAI_HOSO.CHUA_XULY;
        hsba = emrHoSoBenhAnRepository.save(hsba);

        if(createNew) {
            emrLogService.logAction(EmrHoSoBenhAn.class.getName(), hsba.id, MA_HANH_DONG.TAO_MOI, new Date(), userId, 
                                        "", jsonSt);
        }
        
        emrLogService.logAction(EmrHoSoBenhAn.class.getName(), hsba.id, MA_HANH_DONG.CHINH_SUA, new Date(), userId, 
                                    JsonUtil.dumpObject(hsba), jsonSt);
        
        return hsba;
    }
   
    public void archiveHsba(ObjectId id, ObjectId userId) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            emrLogService.logAction(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.LUU_TRU, new Date(), userId, "", "");            
            x.trangThai = TRANGTHAI_HOSO.DA_LUU;
            x.nguoiluutruId = userId;
            x.ngayluutru = new Date();
            x.maluutru = x.mayte;
            emrHoSoBenhAnRepository.save(x);
        });
    }
    
    public void unArchiveHsba(ObjectId id, ObjectId userId) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            emrLogService.logAction(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.MO_LUU_TRU, new Date(), userId, "", "");            
            x.trangThai = TRANGTHAI_HOSO.CHUA_XULY;
            x.nguoimoluutruId = userId;
            x.ngaymoluutru = new Date();
            emrHoSoBenhAnRepository.save(x);
        });
    }
    
    public void deleteHsba(ObjectId id, ObjectId userId) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            emrLogService.logAction(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.XOA, new Date(), userId, "", "");
            x.trangThai = TRANGTHAI_HOSO.DA_XOA;
            emrHoSoBenhAnRepository.save(x);
        });
    }
    
    public List<EmrHoSoBenhAn> getReport(ObjectId emrCoSoKhamBenhId, String maLoaiBenhAn, Date fromDate, Date toDate) {
        var criteria = new Criteria();
        if(emrCoSoKhamBenhId != null) {
            criteria = criteria.and("emrCoSoKhamBenhId").is(emrCoSoKhamBenhId);            
        }
        if(!StringUtils.isEmpty(maLoaiBenhAn)) {
            criteria = criteria.and("emrDmLoaiBenhAn.ma").is(maLoaiBenhAn);
        }
        if(fromDate != null && toDate != null) {
            criteria = criteria.and("ngaytiepnhan").gte(fromDate).lte(toDate);
        }else {
            if(fromDate != null) {
                criteria = criteria.and("ngaytiepnhan").gte(fromDate);
            }
            if(toDate != null) {
                criteria = criteria.and("ngaytiepnhan").lte(toDate);           
            }
        }
        return mongoTemplate.find(new Query(criteria), EmrHoSoBenhAn.class);
    }
    
    public void addEmrFileDinhKems(ObjectId id, List<EmrFileDinhKem> emrFileDinhKems) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            if(x.emrFileDinhKems == null) {
                x.emrFileDinhKems = new ArrayList<>();
            }
            x.emrFileDinhKems.addAll(emrFileDinhKems);
            emrHoSoBenhAnRepository.save(x);
        });
    }
    public void addUserViewHSBA(ObjectId id, ObjectId userId) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
           if (x.dsNguoiXemIds == null) {
        	   x.dsNguoiXemIds = new ArrayList<>();
           }
           if (!x.dsNguoiXemIds.contains(userId)) {
        	   x.dsNguoiXemIds.add(userId);
           }
           emrHoSoBenhAnRepository.save(x);
        });
    }
    
    public void deleteUserViewHSBA(ObjectId id, ObjectId userId) {
        var hsba = emrHoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
           if (x.dsNguoiXemIds == null) {
        	   x.dsNguoiXemIds = new ArrayList<>();
           }
           if (x.dsNguoiXemIds.contains(userId)) {
        	   x.dsNguoiXemIds.remove(userId);
           }
           emrHoSoBenhAnRepository.save(x);
        });
    }
}