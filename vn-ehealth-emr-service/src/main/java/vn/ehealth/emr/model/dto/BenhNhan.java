package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.DbUtils;

import static vn.ehealth.emr.utils.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class BenhNhan  extends BaseModelDTO {
    public String tenDayDu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinh;
    
    public String soTheBhyt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayHetHanTheBhyt;
    
    public String idHis;
    public DiaChi diaChi;
    public DanhMuc dmGioiTinh;
    public DanhMuc dmDanToc;    
    public DanhMuc dmTonGiao;
    public DanhMuc dmNgheNghiep;
    public DanhMuc dmLoaiDoiTuongTaiChinh;
    public String email;
    public String soDienThoai;
    
    static Map<String, String> gioiTinhMap = Map.of(
                "M", "Nam",
                "F", "Nữ",
                "U", "Không xác định"
            );
    
    public BenhNhan() {
        super();
    }
    
    public BenhNhan(Patient obj) {
        super(obj);
        if(obj == null) return;
        
        this.tenDayDu = obj.hasName()? obj.getName().get(0).getText(): "";
        this.ngaySinh = obj.getBirthDate();
        
        var bhyt = FPUtil.findFirst(obj.getIdentifier(), x -> CodeSystemValue.SO_THE_BHYT.equals(x.getSystem()));
        if(bhyt != null) {
            this.soTheBhyt = bhyt.getValue();
            this.ngayHetHanTheBhyt = bhyt.hasPeriod()? bhyt.getPeriod().getEnd() : null;
        }
        
        var idHis = FPUtil.findFirst(obj.getIdentifier(), x -> CodeSystemValue.ID_HIS.equals(x.getSystem()));
        this.idHis = idHis != null? idHis.getValue() : "";
        
        if(obj.hasAddress()) {
            this.diaChi = DiaChi.fromFhirModel(obj.getAddressFirstRep());
        }
        
        if(obj.hasGender()) {
            var gender = obj.getGender().toCode();
            this.dmGioiTinh = new DanhMuc(gender, gioiTinhMap.getOrDefault(gender, ""), CodeSystemValue.GIOI_TINH);
        }
        
        if(obj.hasTelecom()) {
            var phone = FPUtil.findFirst(obj.getTelecom(), 
                                    x -> x.hasSystem() && CodeSystemValue.PHONE.equals(x.getSystem().toCode())); 
            if(phone != null) {
                this.soDienThoai = phone.getValue();
            }
            
            var email = FPUtil.findFirst(obj.getTelecom(), 
                                    x -> x.hasSystem() && CodeSystemValue.EMAIL.equals(x.getSystem().toCode()));
            if(email != null) {
                this.email = email.getValue();
            }
        }
        
        // TODO
        //this.dmTonGiao = ?
        //this.dmDanToc = ?
        //this.dmNgheNghiep = /        
        //this.dmLoaiDoiTuongTaiChinh = ?
    }

    public static BenhNhan fromFhir(Patient obj) {
        if(obj == null) return null;
        return new BenhNhan(obj);
    }
    
    public static BenhNhan fromReference(Reference ref) {
        if(ref != null && ref.hasReference()) {
            
        }
        if(ref != null && ref.hasReference()) {
            var obj = DbUtils.getPatientDao().read(new IdType(ref.getReference()));
            return fromFhir(obj);
        }
        return null;
    }
    
    public static Patient toFhir(BenhNhan dto) {
        if(dto == null) return null;
        var obj = DbUtils.getPatientDao().read(new IdType(dto.id));
        
        if(obj == null) {
            obj = new Patient();
        }
        
        obj.setName(listOf(createHumanName(dto.tenDayDu)));        
        obj.setBirthDate(dto.ngaySinh);
        obj.setIdentifier(listOf(createIdentifier(dto.soTheBhyt, CodeSystemValue.SO_THE_BHYT),
                                createIdentifier(dto.idHis, CodeSystemValue.ID_HIS, null, dto.ngayHetHanTheBhyt)
                          ));
        
        if(dto.dmGioiTinh != null)
            obj.setGender(AdministrativeGender.fromCode(dto.dmGioiTinh.ma));
        
        obj.setAddress(listOf(DiaChi.toFhirModel(dto.diaChi)));
        obj.setTelecom(listOf(createContactPoint(dto.soDienThoai, CodeSystemValue.PHONE),
                              createContactPoint(dto.email, CodeSystemValue.EMAIL)
                        ));
        
        return obj;
    }
}
