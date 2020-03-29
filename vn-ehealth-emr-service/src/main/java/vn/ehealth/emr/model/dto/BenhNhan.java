package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;
import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.emr.utils.MessageUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class BenhNhan  extends BaseModelDTO {
    public String tenDayDu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinh;
    
    public String cmnd;
    public String soTheBhyt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayHetHanTheBhyt;
    
    public DiaChi diaChi;
    public DanhMuc dmGioiTinh;
    public DanhMuc dmDanToc;    
    public DanhMuc dmTonGiao;
    public DanhMuc dmNgheNghiep;
    public DanhMuc dmQuocTich;
    //public DanhMuc dmLoaiDoiTuongTaiChinh;
    public String email;
    public String soDienThoai;
    
    static Map<String, String> gioiTinhMap = mapOf(
                "male", MessageUtils.get("gioitinh.nam"),
                "female", MessageUtils.get("gioitinh.nu"),
                "other", MessageUtils.get("gioitinh.khac"),
                "unknown", MessageUtils.get("gioitinh.khongxacdinh")
            );
    
    public BenhNhan() {
        super();
    }
    
    public BenhNhan(Patient obj) {
        super(obj);
        if(obj == null) return;
        
        this.tenDayDu = obj.hasName()? obj.getName().get(0).getText(): "";
        this.ngaySinh = obj.getBirthDate();
        
        if(obj.hasIdentifier()) {
            var nationalIdentifier = FPUtil.findFirst(obj.getIdentifier(), 
                    x -> IdentifierSystem.CMND.equals(x.getSystem()));
            
            if(nationalIdentifier != null) {
                this.cmnd = nationalIdentifier.getValue();
            }
            
            var mohIdentifier = FPUtil.findFirst(obj.getIdentifier(), 
                    x -> IdentifierSystem.THE_BHYT.equals(x.getSystem()));
            
            if(mohIdentifier != null) {
                this.soTheBhyt = mohIdentifier.getValue();
                this.ngayHetHanTheBhyt = mohIdentifier.hasPeriod()? mohIdentifier.getPeriod().getEnd() : null;
            }
        }
        
        if(obj.hasAddress()) {
            this.diaChi = DiaChi.fromFhirModel(obj.getAddressFirstRep());
        }
        
        if(obj.hasGender()) {
            var gender = obj.getGender().toCode();
            this.dmGioiTinh = new DanhMuc(gender, gioiTinhMap.getOrDefault(gender, ""), CodeSystemValue.GIOI_TINH);
        }
        
        if(obj.hasTelecom()) {
            var phone = FPUtil.findFirst(obj.getTelecom(), 
                                    x -> x.hasSystem() && ContactPointSystem.PHONE.equals(x.getSystem())); 
            if(phone != null) {
                this.soDienThoai = phone.getValue();
            }
            
            var email = FPUtil.findFirst(obj.getTelecom(), 
                                    x -> x.hasSystem() && ContactPointSystem.EMAIL.equals(x.getSystem()));
            if(email != null) {
                this.email = email.getValue();
            }
        }
        
        if(obj.hasExtension())  {
            var ext = findExtensionByURL(obj.getExtension(), ExtensionURL.DAN_TOC);
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                this.dmDanToc = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
            
            ext = findExtensionByURL(obj.getExtension(), ExtensionURL.TON_GIAO);
            
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                this.dmTonGiao = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
            
            ext = findExtensionByURL(obj.getExtension(), ExtensionURL.NGHE_NGHIEP);
            
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                this.dmNgheNghiep = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }            
        }
        
        if(obj.hasModifierExtension()) {
            var ext = findExtensionByURL(obj.getModifierExtension(), ExtensionURL.QUOC_TICH);
            
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                this.dmQuocTich = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
        }
    }

    public static BenhNhan fromFhir(Patient obj) {
        if(obj == null) return null;
        return new BenhNhan(obj);
    }
    
    public static Patient toFhir(BenhNhan dto) {
        if(dto == null) return null;
        
        var obj = new Patient();
        
        obj.setId(dto.id);        
        obj.setName(listOf(createHumanName(dto.tenDayDu)));        
        obj.setBirthDate(dto.ngaySinh);
        
        if(!StringUtils.isBlank(dto.cmnd)) {
            var nationalIdentifier = createIdentifier(dto.cmnd, IdentifierSystem.CMND);
            obj.addIdentifier(nationalIdentifier);
        }
        
        if(!StringUtils.isBlank(dto.soTheBhyt)) {
            var mohIdentifier = createIdentifier(dto.soTheBhyt, IdentifierSystem.THE_BHYT, null, dto.ngayHetHanTheBhyt);
            obj.addIdentifier(mohIdentifier);
        }
        
        if(dto.dmGioiTinh != null)
            obj.setGender(AdministrativeGender.fromCode(dto.dmGioiTinh.ma));
        
        obj.setAddress(listOf(DiaChi.toFhirModel(dto.diaChi)));
        if(!StringUtils.isBlank(dto.soDienThoai)) {
            obj.addTelecom(createContactPoint(dto.soDienThoai, ContactPointSystem.PHONE));
        }
        
        if(!StringUtils.isBlank(dto.email)) {
            obj.addTelecom(createContactPoint(dto.email, ContactPointSystem.EMAIL));
        }

        var danTocExt = createExtension(ExtensionURL.DAN_TOC, DanhMuc.toConcept(dto.dmDanToc, CodeSystemValue.DAN_TOC));
        var tonGiaoExt = createExtension(ExtensionURL.TON_GIAO, DanhMuc.toConcept(dto.dmTonGiao, CodeSystemValue.TON_GIAO));
        var ngheNghiepExt = createExtension(ExtensionURL.NGHE_NGHIEP, DanhMuc.toConcept(dto.dmNgheNghiep, CodeSystemValue.NGHE_NGHIEP));
        var quocTichExt = createExtension(ExtensionURL.QUOC_TICH, DanhMuc.toConcept(dto.dmQuocTich, CodeSystemValue.QUOC_GIA));
        
        obj.setExtension(listOf(danTocExt, tonGiaoExt, ngheNghiepExt));
        obj.setModifierExtension(listOf(quocTichExt));
        
        return obj;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.Patient;
    }
}
