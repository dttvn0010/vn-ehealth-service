package vn.ehealth.cdr.model;

import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "benh_nhan")
public class BenhNhan {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }    
   
    public int trangThai;
    
    public DanhMuc dmGioiTinh;
    
    public DanhMuc dmDanToc;
    
    public DanhMuc dmTonGiao;
    
    public DanhMuc dmQuocGia;
    
    public DanhMuc dmNgheNghiep;
    
    public DanhMuc dmPhuongXa;
    
    public DanhMuc dmQuanHuyen;
    
    public DanhMuc dmTinhThanh;
    
    public DanhMuc dmNgheNghiepBo;
    
    public DanhMuc dmNgheNghiepMe;
    
    public DanhMuc dmLoaiDoiTuongTaiChinh;
    
    public String idDinhDanhChinh;

    public String idDinhDanhPhu;

    public String idhis;

    public String tenDayDu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysinh;

    public String diachi;
    
    public String sodienthoai;
    
    public String email;

    public String noilamviec;

    public String sobhyt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayHetHanTheBhyt;

    public String hoTenBo;

    public String hoTenMe;

    public String tenNguoiBaoTin;

    public String diaChiNguoiBaoTin;

    public String soDienThoaiNguoiBaoTin;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaBo;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaMe;
    
    public String trinhDoVanHoaCuaBo;
    
    public String trinhDoVanHoaCuaMe;
    
    @JsonIgnore
    public ObjectId emrPersonId;
    
    private static Map<String, String> gioiTinhCodeMap = mapOf(
            "M", "male",
            "F", "female",
            "O", "other",
            "U", "unknown",
            "01", "female",
            "1", "female",
            "02", "male",
            "2", "male"
        );
    
   
    @JsonIgnore
    public Address getAddress() {
        var obj = new Address();
        if(diachi != null) {
            obj.setText(diachi);
            obj.addLine(diachi);
        }
        
        if(dmQuanHuyen != null) {
            obj.setDistrict(dmQuanHuyen.ten);
        }
        
        if(dmTinhThanh != null) {
            obj.setCity(dmTinhThanh.ten);
        }
        
        var tinhThanhExt = createExtension("city", DanhMuc.toConcept(dmTinhThanh, CodeSystemValue.DVHC));        
        var quanHuyenExt = createExtension("district", DanhMuc.toConcept(dmQuanHuyen, CodeSystemValue.DVHC));        
        var xaPhuongExt = createExtension("ward", DanhMuc.toConcept(dmPhuongXa, CodeSystemValue.DVHC));
        
        var extension = obj.addExtension();
        extension.setUrl(ExtensionURL.DVHC);
        extension.addExtension(tinhThanhExt);
        extension.addExtension(quanHuyenExt);
        extension.addExtension(xaPhuongExt);
        return obj;
    }
   
    public Patient toFhir() {
        
        var obj = new Patient();
        obj.setName(listOf(createHumanName(tenDayDu)));        
        obj.setBirthDate(ngaysinh);
        
        if(!StringUtils.isBlank(sobhyt)) {
            var mohIdentifier = createIdentifier(sobhyt, IdentifierSystem.DINH_DANH_Y_TE, 
                                                    null, ngayHetHanTheBhyt);
            obj.addIdentifier(mohIdentifier);
        }
        
        if(!StringUtils.isBlank(idDinhDanhPhu)) {
            var nationalIdentifier = createIdentifier(idDinhDanhPhu, IdentifierSystem.CMND);
            obj.addIdentifier(nationalIdentifier);
        }
        
        
        if(dmGioiTinh != null) {
            var genderCode = gioiTinhCodeMap.get(dmGioiTinh.ma);
            obj.setGender(AdministrativeGender.fromCode(genderCode));
        }
        
        obj.setAddress(listOf(getAddress()));
        if(!StringUtils.isBlank(sodienthoai)) {
            obj.addTelecom(createContactPoint(sodienthoai, ContactPointSystem.PHONE));
        }
        
        if(!StringUtils.isBlank(email)) {
            obj.addTelecom(createContactPoint(email, ContactPointSystem.EMAIL));
        }

        var danTocExt = createExtension(ExtensionURL.DAN_TOC, DanhMuc.toConcept(dmDanToc, CodeSystemValue.DAN_TOC));
        var tonGiaoExt = createExtension(ExtensionURL.TON_GIAO, DanhMuc.toConcept(dmTonGiao, CodeSystemValue.TON_GIAO));
        var ngheNghiepExt = createExtension(ExtensionURL.NGHE_NGHIEP, DanhMuc.toConcept(dmNgheNghiep, CodeSystemValue.NGHE_NGHIEP));
        var quocTichExt = createExtension(ExtensionURL.QUOC_TICH, DanhMuc.toConcept(dmQuocGia, CodeSystemValue.QUOC_GIA));
        
        obj.setExtension(listOf(danTocExt, tonGiaoExt, ngheNghiepExt));
        obj.setModifierExtension(listOf(quocTichExt));
        
        return obj;
    }    
        
}
