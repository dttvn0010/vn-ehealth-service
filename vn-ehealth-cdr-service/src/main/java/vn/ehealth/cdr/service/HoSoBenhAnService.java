package vn.ehealth.cdr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import vn.ehealth.auth.model.User;
import vn.ehealth.auth.service.UserService;
import vn.ehealth.cdr.model.BenhNhan;
import vn.ehealth.cdr.model.CoSoKhamBenh;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.repository.HoSoBenhAnRepository;
import vn.ehealth.cdr.utils.CDRUtils;
import vn.ehealth.cdr.utils.JsonUtil;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.cdr.utils.CDRConstants.MA_HANH_DONG;
import vn.ehealth.cdr.utils.CDRConstants.NGUON_DU_LIEU;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_HOSO;

@Service
public class HoSoBenhAnService {
   
    private SimpleDateFormat sdf = CDRUtils.createSimpleDateFormat("dd/MM/yyyy HH:mm");
            
    @Autowired private HoSoBenhAnRepository hoSoBenhAnRepository;
    
    @Autowired private LogService logService;
    @Autowired private UserService userService;
    
    @Autowired private MongoTemplate mongoTemplate;
    
    public Optional<HoSoBenhAn> getByMaYte(String maYte) {
        return hoSoBenhAnRepository.findByMaYte(maYte);
    }
    
    public Optional<HoSoBenhAn> getByMaTraoDoi(String maTraoDoi) {
        return hoSoBenhAnRepository.findByMaTraoDoi(maTraoDoi);
    }
    
    public List<String> getAllIds() {
        var criteria = new Criteria();
        var query = new Query(criteria);
        query.fields().include("_id");
        var lst = mongoTemplate.find(query, HoSoBenhAn.class);
        return DataConvertUtil.transform(lst, x -> x.getId());
    }
    
    public long countHoSo(ObjectId userId, ObjectId coSoKhamBenhId, int trangThai, String maYte) {
        var query = new Query(Criteria.where("coSoKhamBenhRef.objectId").is(coSoKhamBenhId)
                                        .and("trangThai").is(trangThai)
                                        .and("maYte").regex(maYte)
                             );
        
        return mongoTemplate.count(query, HoSoBenhAn.class);
    }
    
    public List<HoSoBenhAn> getDsHoSo(ObjectId userId, ObjectId coSoKhamBenhId, int trangThai, String maYte, int offset, int limit) {
        var sort = new Sort(Sort.Direction.DESC, "ngayTao");
        
        var query = new Query(Criteria.where("coSoKhamBenhRef.objectId").is(coSoKhamBenhId)
        								.and("trangThai").is(trangThai)
                                        .and("maYte").regex(maYte)
                             ).with(sort).skip(offset).limit(limit);
        
        return mongoTemplate.find(query, HoSoBenhAn.class);
    }
    
    public Optional<HoSoBenhAn> getById(ObjectId id){
        return hoSoBenhAnRepository.findById(id);
    }
    
    public String getHsgoc(ObjectId id) {
        var logs = logService.getLogs(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.THEM_SUA, false, 0, 1);
        if(logs.size() > 0) {
            return logs.get(0).ghiChu;
        }
        return "";
    }
    
    public long countHistory(ObjectId id) {
        return logService.countLogs(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.THEM_SUA, false);   
    }
    
    public List<Object> getHistory(ObjectId id, int offset, int limit) {
        var logs = logService.getLogs(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.THEM_SUA, false, offset, limit);
        
        var result = new ArrayList<>();
        for(var log : logs) {
            var hsba = JsonUtil.parseObject(log.noiDung, HoSoBenhAn.class);
            String nguoiThucHien = "", ngayThucHien = "";
            
            if(log.nguoiThucHienId != null) {
                var user = userService.getById(log.nguoiThucHienId);
                nguoiThucHien = user.map(x -> x.getDisplay()).orElse("");
            }
            
            if(log.ngayThucHien != null) {
                ngayThucHien = sdf.format(log.ngayThucHien);
            }            
                    
            result.add(Map.of("hsba", hsba, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
        }
        
        return result;
    }
    
    public HoSoBenhAn save(HoSoBenhAn hsba) {
        return hoSoBenhAnRepository.save(hsba);
    }
    
    public HoSoBenhAn createOrUpdateFromHIS(@Nonnull BenhNhan benhNhan, @Nonnull CoSoKhamBenh coSoKhamBenh, @Nonnull HoSoBenhAn hsba, String jsonSt) {
        hsba.id = hoSoBenhAnRepository.findByMaTraoDoi(hsba.maTraoDoi).map(x -> x.id).orElse(null);
        boolean createNew = hsba.id == null;
        
        hsba.benhNhanRef = BenhNhan.toRef(benhNhan);        
        hsba.coSoKhamBenhRef = CoSoKhamBenh.toEmrRef(coSoKhamBenh);
        
        if(createNew) {
            hsba.ngayTao = new Date();
            hsba.nguoiTaoRef = null;
        }else {
            hsba.ngaySua = new Date();
            hsba.nguoiSuaRef = null;
        }
                
        hsba.nguonDuLieu = NGUON_DU_LIEU.TU_HIS;
        hsba.trangThai = TRANGTHAI_HOSO.CHUA_XULY;
        hsba = hoSoBenhAnRepository.save(hsba);

        logService.logAction(HoSoBenhAn.class.getName(), hsba.id, MA_HANH_DONG.THEM_SUA, new Date(), 
                                    null, JsonUtil.dumpObject(hsba), jsonSt);
        
        return hsba;
    }
   
    public void archiveHsba(ObjectId id, @Nonnull User user) {
        var hsba = hoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            logService.logAction(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.LUU_TRU, new Date(), user.id, "", "");            
            x.trangThai = TRANGTHAI_HOSO.DA_LUU;
            x.nguoiLuuTruRef = EmrRef.fromUser(user);
            x.ngayLuuTru = new Date();
            x.maLuuTru = x.maYte;
            hoSoBenhAnRepository.save(x);
        });
    }
    
    public void unArchiveHsba(ObjectId id, @Nonnull User user) {
        var hsba = hoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            logService.logAction(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.MO_LUU_TRU, new Date(), user.id, "", "");            
            x.trangThai = TRANGTHAI_HOSO.CHUA_XULY;
            x.nguoiMoLuTruRef = EmrRef.fromUser(user);
            x.ngayMoLuuTru = new Date();
            hoSoBenhAnRepository.save(x);
        });
    }
    
    public void deleteHsba(ObjectId id, @Nonnull User user) {
        var hsba = hoSoBenhAnRepository.findById(id);
        hsba.ifPresent(x -> {
            logService.logAction(HoSoBenhAn.class.getName(), id, MA_HANH_DONG.XOA, new Date(), user.id, "", "");
            x.trangThai = TRANGTHAI_HOSO.DA_XOA;
            x.nguoiXoaRef =  EmrRef.fromUser(user);
            x.ngayXoa = new Date();
            hoSoBenhAnRepository.save(x);
        });
    }
}