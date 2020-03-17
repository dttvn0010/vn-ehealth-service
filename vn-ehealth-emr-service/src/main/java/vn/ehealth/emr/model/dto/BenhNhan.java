package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.patient.entity.PatientEntity;

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
    
    public BenhNhan(PatientEntity ent) {
        super(ent);
        if(ent == null) return;
        
        this.tenDayDu = ent.getName();
        this.ngaySinh = ent.birthDate;
        
        var bhyt = ent.getIdentifierBySystem(CodeSystem.SO_THE_BHYT);
        if(bhyt != null) {
            this.soTheBhyt = bhyt.value;
            this.ngayHetHanTheBhyt = bhyt.period != null? bhyt.period.end : null;
        }
        
        this.idHis = ent.getIdentifierValueBySystem(CodeSystem.ID_HIS);
        if(ent.address != null && ent.address.size() > 0) {
            this.diaChi = DiaChi.fromEntity(ent.address.get(0));
        }
        this.dmGioiTinh = new DanhMuc(ent.gender, gioiTinhMap.getOrDefault(ent.gender, ""), CodeSystem.GIOI_TINH);
        this.soDienThoai = ent.getPhone();
        this.email = ent.getEmail();
        this.dmTonGiao = new DanhMuc(ent.religion);
        this.dmDanToc = new DanhMuc(ent.ethnic);
        this.dmNgheNghiep = new DanhMuc(ent.job);        
        this.dmLoaiDoiTuongTaiChinh = new DanhMuc(ent.getCategoryBySystem(CodeSystem.DOI_TUONG_TAI_CHINH));
    }

    public static BenhNhan fromEntity(PatientEntity ent) {
        if(ent == null) return null;
        return new BenhNhan(ent);
    }
    
    public static BenhNhan fromReference(BaseReference ref) {
        if(ref != null && ref.reference != null) {
            var ent = ServiceFactory.getPatientService().getByFhirId(ref.reference).orElseThrow();
            return fromEntity(ent);
        }
        return null;
    }
    
    public static PatientEntity toEntity(BenhNhan dto) {
        if(dto == null) return null;
        var ent = ServiceFactory.getPatientService().getByFhirId(dto.fhirId).orElse(null);
        
        if(ent == null) {
            ent = new PatientEntity();
            ent.fhirId = StringUtil.generateUID();
        }
        
        ent.name = listOf(new BaseHumanName(dto.tenDayDu));
        ent.birthDate = dto.ngaySinh;        
        
        ent.identifier = listOf(
                            new BaseIdentifier(dto.soTheBhyt, CodeSystem.SO_THE_BHYT, null, dto.ngayHetHanTheBhyt),
                            new BaseIdentifier(dto.idHis, CodeSystem.ID_HIS)
                        );
        
        ent.gender = dto.dmGioiTinh != null? dto.dmGioiTinh.ma : null;
        ent.address = dto.diaChi != null? listOf(DiaChi.toEntity(dto.diaChi)) : new ArrayList<>();        
        ent.telecom = listOf(new BaseContactPoint(dto.soDienThoai, "phone"), new BaseContactPoint(dto.email, "email"));
        ent.ethnic = DanhMuc.toBaseCodeableConcept(dto.dmDanToc, CodeSystem.DAN_TOC);
        ent.religion = DanhMuc.toBaseCodeableConcept(dto.dmTonGiao, CodeSystem.TON_GIAO);
        ent.job = DanhMuc.toBaseCodeableConcept(dto.dmNgheNghiep, CodeSystem.NGHE_NGHIEP);
        ent.category = listOf(DanhMuc.toBaseCodeableConcept(dto.dmLoaiDoiTuongTaiChinh,CodeSystem.DOI_TUONG_TAI_CHINH));
        return ent;
    }
}
