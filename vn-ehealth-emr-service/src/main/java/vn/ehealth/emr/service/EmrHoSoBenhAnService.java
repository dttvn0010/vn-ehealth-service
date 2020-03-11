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
import org.springframework.data.domain.Sort;
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
    @Autowired EmrVaoKhoaService emrVaoKhoaService;
    @Autowired EmrBenhNhanService emrBenhNhanService;
    @Autowired EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    @Autowired EmrLogService emrLogService;
    @Autowired UserService userService;
    
    @Autowired MongoTemplate mongoTemplate;
    
    public long countHoSo(ObjectId emrCoSoKhamBenhId, int trangThai, String mayte) {
        var query = new Query(Criteria.where("emrCoSoKhamBenhId").is(emrCoSoKhamBenhId)
                                        .and("trangThai").is(trangThai)
                                        .and("mayte").regex(mayte)
                             );
        
        return mongoTemplate.count(query, EmrHoSoBenhAn.class);
    }
    
    public List<EmrHoSoBenhAn> getDsHoSo(ObjectId emrCoSoKhamBenhId, int trangThai, String mayte, int offset, int limit) {
        var sort = new Sort(Sort.Direction.DESC, "ngaytiepnhan");
        var pageable = new OffsetBasedPageRequest(limit, offset, sort);
        var query = new Query(Criteria.where("emrCoSoKhamBenhId").is(emrCoSoKhamBenhId)
                                        .and("trangThai").is(trangThai)
                                        .and("mayte").regex(mayte)
                             ).with(pageable);
        
        return mongoTemplate.find(query, EmrHoSoBenhAn.class);
    }
    
    public List<EmrHoSoBenhAn> getByEmrBenhNhanId(ObjectId emrBenhNhanId) {
        return emrHoSoBenhAnRepository.findByEmrBenhNhanId(emrBenhNhanId);
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
    
    public void getEmrHoSoBenhAnDetail(@Nonnull EmrHoSoBenhAn hsba) {
        hsba.getEmrVaoKhoas();
        hsba.emrVaoKhoas.forEach(x -> emrVaoKhoaService.getEmrVaoKhoaDetail(x));
        
        hsba.emrHinhAnhTonThuongs = emrHinhAnhTonThuongService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrGiaiPhauBenhs = emrGiaiPhauBenhService.getByEmrHoSoBenhAnId(hsba.id) ;
        hsba.emrThamDoChucNangs = emrThamDoChucNangService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrPhauThuatThuThuats = emrPhauThuatThuThuatService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrChanDoanHinhAnhs = emrChanDoanHinhAnhService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrDonThuocs = emrDonThuocService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrYhctDonThuocs = emrYhctDonThuocService.getByEmrHoSoBenhAnId(hsba.id);
        hsba.emrXetNghiems = emrXetNghiemService.getByEmrHoSoBenhAnId(hsba.id);  
    }
    
    public List<Object> getHistory(ObjectId id) {
        var logs = emrLogService.getLogs(EmrHoSoBenhAn.class.getName(), id, MA_HANH_DONG.CHINH_SUA, false, -1, -1);
        
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
                    
            result.add(Map.of("hsba", hsba, "hsGoc", log.ghiChu, "ngaySua", ngayThucHien, "nguoiSua", nguoiThucHien));
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
    
    public EmrHoSoBenhAn createOrUpdateHIS(@Nonnull EmrHoSoBenhAn hsba, ObjectId userId, String jsonSt) {
        hsba.id = emrHoSoBenhAnRepository.findByMatraodoi(hsba.matraodoi).map(x -> x.id).orElse(null);
        boolean createNew = hsba.id == null;
        
        var emrCoSoKhamBenh = hsba.getEmrCoSoKhamBenh();
        var maCoSoKhamBenh = emrCoSoKhamBenh != null? emrCoSoKhamBenh.ma : "";
        emrCoSoKhamBenh = emrCoSoKhamBenhService.getByMa(maCoSoKhamBenh).orElse(null);
                
        if(emrCoSoKhamBenh == null) {
            throw new RuntimeException("No emrCoSoKhamBenh for ma : " + maCoSoKhamBenh);
        }
        
        var emrBenhNhan = hsba.getEmrBenhNhan();
        if(emrBenhNhan == null) {
            throw new RuntimeException("No patient info");
        }
        
        if(StringUtils.isEmpty(emrBenhNhan.iddinhdanhchinh)) {
            emrBenhNhan.iddinhdanhchinh = emrBenhNhan.idhis;
        }
        
        if(StringUtils.isEmpty(emrBenhNhan.iddinhdanhchinh)) {
            throw new RuntimeException("Empty emrBenhNhan.iddinhdanhchinh");
        }        
        
        emrBenhNhan = emrBenhNhanService.createOrUpdate(emrBenhNhan);
        
        hsba.emrBenhNhanId = emrBenhNhan.id;        
        hsba.emrCoSoKhamBenhId = emrCoSoKhamBenh.id;
        
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
        
        if(!createNew) {
            emrVaoKhoaService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrHinhAnhTonThuongService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrGiaiPhauBenhService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrThamDoChucNangService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrPhauThuatThuThuatService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrChanDoanHinhAnhService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrDonThuocService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrYhctDonThuocService.deleteAllByEmrHoSoBenhAnId(hsba.id);
            emrXetNghiemService.deleteAllByEmrHoSoBenhAnId(hsba.id);
        }
        
        for(int i = 0; hsba.emrVaoKhoas != null && i < hsba.emrVaoKhoas.size(); i++) {
            var vk = hsba.emrVaoKhoas.get(i);
            vk.emrHoSoBenhAnId = hsba.id;
            vk.emrBenhNhanId = hsba.emrBenhNhanId;
            vk.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            vk = emrVaoKhoaService.createOrUpdate(vk);
            hsba.emrVaoKhoas.set(i, vk);            
        }
        
        for(int i = 0; hsba.emrHinhAnhTonThuongs != null && i < hsba.emrHinhAnhTonThuongs.size(); i++) {
            var hatt = hsba.emrHinhAnhTonThuongs.get(i);
            hatt.emrHoSoBenhAnId = hsba.id;
            hatt.emrBenhNhanId = hsba.emrBenhNhanId;
            hatt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            hatt = emrHinhAnhTonThuongService.createOrUpdate(hatt);
            hsba.emrHinhAnhTonThuongs.set(i, hatt);
        }
        
        for(int i = 0; hsba.emrGiaiPhauBenhs != null && i < hsba.emrGiaiPhauBenhs.size(); i++) {
            var gpb = hsba.emrGiaiPhauBenhs.get(i);
            gpb.emrHoSoBenhAnId = hsba.id;
            gpb.emrBenhNhanId = hsba.emrBenhNhanId;
            gpb.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            gpb = emrGiaiPhauBenhService.createOrUpdate(gpb);
            hsba.emrGiaiPhauBenhs.set(i, gpb);
        }
        
        for(int i = 0; hsba.emrThamDoChucNangs != null && i < hsba.emrThamDoChucNangs.size(); i++) {
            var tdcn = hsba.emrThamDoChucNangs.get(i);
            tdcn.emrHoSoBenhAnId = hsba.id;
            tdcn.emrBenhNhanId = hsba.emrBenhNhanId;
            tdcn.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            tdcn = emrThamDoChucNangService.createOrUpdate(tdcn);
            hsba.emrThamDoChucNangs.set(i, tdcn);
        }
        
        for(int i = 0; hsba.emrPhauThuatThuThuats != null && i < hsba.emrPhauThuatThuThuats.size(); i++) {
            var pttt = hsba.emrPhauThuatThuThuats.get(i);
            pttt.emrHoSoBenhAnId = hsba.id;
            pttt.emrBenhNhanId = hsba.emrBenhNhanId;
            pttt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            pttt = emrPhauThuatThuThuatService.createOrUpdate(pttt);
            hsba.emrPhauThuatThuThuats.set(i, pttt);
        }
        
        for(int i = 0; hsba.emrChanDoanHinhAnhs != null && i < hsba.emrChanDoanHinhAnhs.size(); i++) {
            var cdha = hsba.emrChanDoanHinhAnhs.get(i);
            cdha.emrHoSoBenhAnId = hsba.id;
            cdha.emrBenhNhanId = hsba.emrBenhNhanId;
            cdha.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            cdha = emrChanDoanHinhAnhService.createOrUpdate(cdha);
            hsba.emrChanDoanHinhAnhs.set(i, cdha);
        }
        
        for(int i = 0; hsba.emrDonThuocs != null && i < hsba.emrDonThuocs.size(); i++) {
            var donthuoc = hsba.emrDonThuocs.get(i);
            donthuoc.emrHoSoBenhAnId = hsba.id;
            donthuoc.emrBenhNhanId = hsba.emrBenhNhanId;
            donthuoc.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            donthuoc = emrDonThuocService.createOrUpdate(donthuoc);
            hsba.emrDonThuocs.set(i, donthuoc);
        }
        
        for(int i = 0; hsba.emrYhctDonThuocs != null && i < hsba.emrYhctDonThuocs.size(); i++) {
            var yhctdt =  hsba.emrYhctDonThuocs.get(i);
            yhctdt.emrHoSoBenhAnId = hsba.id;
            yhctdt.emrBenhNhanId = hsba.emrBenhNhanId;
            yhctdt.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            yhctdt = emrYhctDonThuocService.createOrUpdate(yhctdt);
            hsba.emrYhctDonThuocs.set(i, yhctdt);            
        }
        
        for(int i = 0; hsba.emrXetNghiems != null && i < hsba.emrXetNghiems.size(); i++) {
            var xn = hsba.emrXetNghiems.get(i);
            xn.emrHoSoBenhAnId = hsba.id;
            xn.emrBenhNhanId = hsba.emrBenhNhanId;
            xn.emrCoSoKhamBenhId = hsba.emrCoSoKhamBenhId;
            xn = emrXetNghiemService.createOrUpdate(xn);
            hsba.emrXetNghiems.set(i, xn);
        }
        
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
}