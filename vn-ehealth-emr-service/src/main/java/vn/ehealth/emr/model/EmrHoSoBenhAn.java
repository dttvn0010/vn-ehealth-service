package vn.ehealth.emr.model;

import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.service.EmrServiceFactory;
import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createPeriod;

@JsonInclude(Include.NON_NULL)
@Document(collection="emr_ho_so_benh_an")
public class EmrHoSoBenhAn {    
    @Id public ObjectId id;
            
    public EmrDmContent emrDmLoaiBenhAn;    
    public ObjectId emrBenhNhanId;    
    public ObjectId emrCoSoKhamBenhId;
    
    @JsonIgnore
    private Map<String, Object> _emrBenhNhan;
    
    @JsonIgnore
    private Map<String, Object> _emrCoSoKhamBenh;
        
    public int nguonDuLieu;    
    public int trangThai;
    public String mayte;
    public String maluutru;
    public String matraodoi;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaytiepnhan;
    
    public String nguoitiepnhan;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaytao;
    
    public ObjectId nguoitaoId;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysua;
    
    public ObjectId nguoisuaId;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayluutru;
    
    public ObjectId nguoiluutruId;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaymoluutru;
    
    public ObjectId nguoimoluutruId;    
    
    public EmrQuanLyNguoiBenh emrQuanLyNguoiBenh;
    
    public EmrTinhTrangRaVien emrTinhTrangRaVien;
    
    public Map<String, Object> emrBenhAn;
        
    public List<EmrVaoKhoa> emrVaoKhoas;
    
    public Map<String, Object> emrChanDoan;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaykybenhan;
    
    public EmrCanboYte bacsylambenhan;
    
    public String nguoigiaohoso;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiaohoso;

    public String nguoinhanhoso;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaynhanhoso;

    public EmrCanboYte bacsydieutri;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaybacsydieutriky;

    @JsonInclude(Include.NON_NULL)    
    public static class EmrToDieuTri {
        public String ma;
        public String ten;
        public int soluong;
    }
    
    public List<EmrToDieuTri> sotodieutri = new ArrayList<>();
    
    public List<EmrFileDinhKem> emrFileDinhKems = new ArrayList<>();
    
    public List<ObjectId> dsNguoiXemIds = new ArrayList<>();
        
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public EmrDmContent getEmrDmLoaiBenhAn() {
        return emrDmLoaiBenhAn;
    }
    
    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }
    
    public EmrBenhNhan getEmrBenhNhan() {
        return EmrServiceFactory.getEmrBenhNhanService().getById(emrBenhNhanId).orElse(null);
    }
    
    public void setEmrBenhNhan(Map<String, Object> emrBenhNhan) {
        this._emrBenhNhan = emrBenhNhan;
    }
    
    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public EmrCoSoKhamBenh getEmrCoSoKhamBenh() {
        return EmrServiceFactory.getEmrCoSoKhamBenhService().getById(emrCoSoKhamBenhId).orElse(null);
    }
    
    public void setEmrCoSoKhamBenh(Map<String, Object> emrCoSoKhamBenh) {
        this._emrCoSoKhamBenh = emrCoSoKhamBenh;
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
    
    public String getNguoitaoId() {
        return ObjectIdUtil.idToString(nguoitaoId);
    }
    
    public void setNguoitaoId(String nguoitaoId) {
        this.nguoitaoId = ObjectIdUtil.stringToId(nguoitaoId);
    }
    
    public String getNguoisuaId() {
        return ObjectIdUtil.idToString(nguoisuaId);
    }
    
    public void setNguoisuaId(String nguoisuaId) {
        this.nguoitaoId = ObjectIdUtil.stringToId(nguoisuaId);
    }
    
    public String getNguoiluutruId() {
        return ObjectIdUtil.idToString(nguoiluutruId);
    }
    
    public void setNguoiluutruId(String nguoiluutruId) {
        this.nguoiluutruId = ObjectIdUtil.stringToId(nguoiluutruId);
    }
    
    public String getNguoimoluutruId() {
        return ObjectIdUtil.idToString(nguoimoluutruId);
    }
    
    public void setNguoimoluutruId(String nguoimoluutruId) {
        this.nguoimoluutruId = ObjectIdUtil.stringToId(nguoimoluutruId);
    }
    
    @JsonInclude(Include.NON_NULL)
    public static class EmrTuoiBenhNhan {
        final public static int YEAR = 1;
        final public static int MONTH = 2;
        final public static int DAY = 3;
        
        public Integer tuoi;
        public Integer donvi;
        
        public EmrTuoiBenhNhan() {
        }
        
        public EmrTuoiBenhNhan(int tuoi, int donvi) {
            this.tuoi = tuoi;
            this.donvi = donvi;                
        }
    }
    
    public EmrTuoiBenhNhan getTuoiBenhNhan() {
        var emrBenhNhan = EmrServiceFactory.getEmrBenhNhanService().getById(emrBenhNhanId).orElseThrow();
        
        if(emrBenhNhan == null
            || emrBenhNhan.ngaysinh == null
            || emrQuanLyNguoiBenh == null 
            || emrQuanLyNguoiBenh.ngaygioravien == null) {
            return new EmrTuoiBenhNhan();
        }
        
        var d1 = new java.sql.Date(emrBenhNhan.ngaysinh.getTime()).toLocalDate();
        var d2 = new java.sql.Date(emrQuanLyNguoiBenh.ngaygioravien.getTime()).toLocalDate();
        
        var m1 = YearMonth.from(d1);
        var m2 = YearMonth.from(d2);
        
        var y1 = Year.from(d1);
        var y2 = Year.from(d2);
        
        int days = (int) d1.until(d2, ChronoUnit.DAYS);
        int months = (int) m1.until(m2, ChronoUnit.MONTHS);
        int years = (int) y1.until(y2, ChronoUnit.YEARS);
        
        if(d1.getDayOfMonth() > d2.getDayOfMonth()) 
            months -= 1;
        
        if(d1.getMonthValue() > d2.getMonthValue() || (d1.getMonthValue() == d2.getMonthValue() && d1.getDayOfMonth() > d2.getDayOfMonth())) 
            years -= 1;
        
        if(months < 1) {
            return new EmrTuoiBenhNhan(days, EmrTuoiBenhNhan.DAY);
        }else if(months < 36) {
            return new EmrTuoiBenhNhan(months, EmrTuoiBenhNhan.MONTH);
        }else {            
            return new EmrTuoiBenhNhan(years, EmrTuoiBenhNhan.YEAR);
        }
    }

    public String getKhoaRaVien() {
        
        if(emrVaoKhoas != null && emrVaoKhoas.size() > 0) {
            var emrKhoaRaVien = emrVaoKhoas.get(emrVaoKhoas.size() - 1);
            var khoaRaVien = emrKhoaRaVien.tenkhoa;
            if(StringUtils.isEmpty(khoaRaVien) && emrKhoaRaVien.emrDmKhoaDieuTri != null) {
                khoaRaVien = emrKhoaRaVien.emrDmKhoaDieuTri.ten;
            }
            return khoaRaVien;
        }
        
        return "";
    }
    
    @JsonIgnore
    public Encounter getEncounterInDB() {
    	var params = mapOf(
    	                "identifier.value", (Object) mayte,
    	                "identifier.system", IdentifierSystem.MA_HO_SO    	                
	                );
    	
    	var criteria = MongoUtils.createCriteria(params);
    	return DaoFactory.getEncounterDao().getResource(criteria);    	
    }
    
    @JsonIgnore
    public List<Encounter> toFhir() {
        var emrBenhNhan = getEmrBenhNhan();
        var emrCoSoKhamBenh = getEmrCoSoKhamBenh();        
        if(emrBenhNhan == null || emrCoSoKhamBenh == null) return null;
        
        var patient = emrBenhNhan.getPatientInDB();
        var serviceProvider = emrCoSoKhamBenh.getOrganizationInDB();        
        if(patient == null || serviceProvider == null) return new ArrayList<>();
        
        var enc = new Encounter();
        enc.setIdentifier(listOf(createIdentifier(mayte, IdentifierSystem.MA_HO_SO)));        
        enc.setSubject(FhirUtil.createReference(patient));
        enc.setServiceProvider(FhirUtil.createReference(serviceProvider));        
        
        if(emrQuanLyNguoiBenh != null) {
            enc.setPeriod(createPeriod(emrQuanLyNguoiBenh.ngaygioravien, emrQuanLyNguoiBenh.ngaygioravien));
        }
        
        var loaiBenhAn = EmrDmContent.toConcept(emrDmLoaiBenhAn, CodeSystemValue.LOAI_KHAM_BENH);
        enc.setType(listOf(loaiBenhAn));
        
        var lst = listOf(enc);
        
        if(emrVaoKhoas != null) {
            for(var vk : emrVaoKhoas) {                
                var vkEnc = new Encounter();
                vkEnc.setSubject(enc.getSubject());
                var type = EmrDmContent.toConcept(vk.emrDmKhoaDieuTri, CodeSystemValue.KHOA_DIEU_TRI);
                vkEnc.setType(listOf(type));
                vkEnc.setServiceProvider(vkEnc.getServiceProvider());
                vkEnc.setPeriod(createPeriod(vk.ngaygiovaokhoa, vk.ngayketthucdieutri));
                
                if(vk.bacsidieutri != null) {
                    var participant = vkEnc.addParticipant();
                    participant.setIndividual(EmrCanboYte.toRef(bacsydieutri));         
                }
                lst.add(vkEnc);
            }
        }
        return lst;
    }
}
