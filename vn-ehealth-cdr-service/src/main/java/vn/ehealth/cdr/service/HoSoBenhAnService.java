package vn.ehealth.cdr.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import vn.ehealth.hl7.fhir.core.util.FPUtil;
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
    
    public HoSoBenhAn getByMaYte(String maYte) {
        var criteria = Criteria.where("maYte").is(maYte)
                                .and("trangThai").ne(TRANGTHAI_HOSO.DA_XOA);
        
        return mongoTemplate.findOne(new Query(criteria), HoSoBenhAn.class);
    }
    
    public HoSoBenhAn getByMaTraoDoi(String maTraoDoi) {
        var criteria = Criteria.where("maTraoDoi").is(maTraoDoi)
                .and("trangThai").ne(TRANGTHAI_HOSO.DA_XOA);

        return mongoTemplate.findOne(new Query(criteria), HoSoBenhAn.class);
    }
    
    public List<String> getAllIds() {
        var criteria = new Criteria();
        var query = new Query(criteria);
        query.fields().include("_id");
        var lst = mongoTemplate.find(query, HoSoBenhAn.class);
        return DataConvertUtil.transform(lst, x -> x.getId());
    }
    
    public long countHoSo(User user, ObjectId coSoKhamBenhId, int trangThai, String maYte) {
    	var critera =  Criteria.where("coSoKhamBenhRef.objectId").is(coSoKhamBenhId)
                .and("trangThai").is(trangThai)
                .and("maYte").regex(maYte);
        var query = new Query(critera);
        
        if(!user.isAdmin()) {
        	critera.and("dsNguoiXemRef.objectId").is(user.id);
        }
        
        return mongoTemplate.count(query, HoSoBenhAn.class);
    }
    
    public List<HoSoBenhAn> getDsHoSo(User user, ObjectId coSoKhamBenhId, int trangThai, String maYte, int offset, int limit) {
        var sort = new Sort(Sort.Direction.DESC, "ngayTao");
        var critera =  Criteria.where("coSoKhamBenhRef.objectId").is(coSoKhamBenhId)
				.and("trangThai").is(trangThai)
                .and("maYte").regex(maYte);
        
        if(!user.isAdmin()) {
        	critera.and("dsNguoiXemRef.objectId").is(user.id);
        }
        
        var query = new Query(critera).with(sort).skip(offset).limit(limit);
        
        return mongoTemplate.find(query, HoSoBenhAn.class);
    }
    
    public HoSoBenhAn getById(ObjectId id){
        var hsba = hoSoBenhAnRepository.findById(id);
        if(hsba.isPresent() && hsba.get().trangThai != TRANGTHAI_HOSO.DA_XOA) {
            return hsba.get();
        }
        return null;
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
        var hsbaOld = getByMaTraoDoi(hsba.maTraoDoi);
        hsba.id = hsbaOld != null? hsbaOld.id : null;
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
    
    public void addNguoiXem(ObjectId id, String[] usernames) {
    	var hsba = hoSoBenhAnRepository.findById(id);
    	hsba.ifPresent(x -> {
    		if(x.dsNguoiXemRef == null) {
        		x.dsNguoiXemRef = new ArrayList<>();
        	}
        	for(String username : usernames) {
        		if(!FPUtil.anyMatch(x.dsNguoiXemRef, ref -> username.equals(ref.identifier))) {
        			var user = userService.getByUsername(username).orElse(null);
        			if(user != null) {
        				x.dsNguoiXemRef.add(EmrRef.fromUser(user));
        			}
        		}
        	}
        	hoSoBenhAnRepository.save(x);
    	});    	
    }
    
    public void xoaNguoiXem(ObjectId id, String[] usernames) {
    	var hsba = hoSoBenhAnRepository.findById(id);
    	hsba.ifPresent(x -> {
    		if(x.dsNguoiXemRef == null) {
        		x.dsNguoiXemRef = new ArrayList<>();
        	}
        	for(String username : usernames) {
        		x.dsNguoiXemRef = FPUtil.filter(x.dsNguoiXemRef, ref -> !username.equals(ref.identifier));
        	}
        	hoSoBenhAnRepository.save(x);
    	});    	
    }
}