package vn.ehealth.emr.model;

import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.BenhNhan;
import vn.ehealth.emr.model.dto.DanhMuc;
import vn.ehealth.emr.model.dto.DiaChi;
import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_benh_nhan")
public class EmrBenhNhan {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }    
   
    public int trangThai;
    
    public EmrDmContent emrDmGioiTinh;
    
    public EmrDmContent emrDmDanToc;
    
    public EmrDmContent emrDmTonGiao;
    
    public EmrDmContent emrDmQuocGia;
    
    public EmrDmContent emrDmNgheNghiep;
    
    public EmrDmContent emrDmPhuongXa;
    
    public EmrDmContent emrDmQuanHuyen;
    
    public EmrDmContent emrDmTinhThanh;
    
    public EmrDmContent emrDmNgheNghiepBo;
    
    public EmrDmContent emrDmNgheNghiepMe;
    
    public EmrDmContent emrDmLoaiDoiTuongTaiChinh;
    
    public String iddinhdanhchinh;

    public String iddinhdanhphu;

    public String idhis;

    public String tendaydu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysinh;

    public String diachi;

    public String noilamviec;

    public String sobhyt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayhethanthebhyt;

    public String hotenbo;

    public String hotenme;

    public String tennguoibaotin;

    public String diachinguoibaotin;

    public String sodienthoainguoibaotin;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaBo;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaMe;
    
    public String trinhDoVanHoaCuaBo;
    
    public String trinhDoVanHoaCuaMe;
    
    public ObjectId emrPersonId;
    
    @JsonIgnore
    public String getFhirId() {
        var params = mapOf("identifier", new TokenParam(IdentifierSystem.THE_BHYT, sobhyt));
        var fhirObj = (Patient) DaoFactory.getPatientDao().searchOne(params);
        if(fhirObj != null) {
            var dto = _toDto();
            fhirObj = BenhNhan.toFhir(dto);
            fhirObj = DaoFactory.getPatientDao().create(fhirObj);
        }
        return fhirObj.getId();
    }
    
    private static Map<String, String> gioiTinhCodeMap = mapOf(
            "M", "male",
            "F", "female",
            "O", "other",
            "U", "unknown"
        );
    
    private static Map<String, String> gioiTinhMap = mapOf(
            "M", MessageUtils.get("gioitinh.nam"),
            "F", MessageUtils.get("gioitinh.nu"),
            "O", MessageUtils.get("gioitinh.khac"),
            "U", MessageUtils.get("gioitinh.khongxacdinh")
        );
    
    private BenhNhan _toDto() {
        var dto = new BenhNhan();
        dto.tenDayDu = this.tendaydu;
        dto.ngaySinh = this.ngaysinh;
        dto.cmnd = this.iddinhdanhphu;
        dto.soTheBhyt = this.sobhyt;
        dto.ngayHetHanTheBhyt = this.ngayhethanthebhyt;
        
        dto.diaChi = new DiaChi();
        dto.diaChi.diaChiChiTiet = this.diachi;
        dto.diaChi.dmXaPhuong = this.emrDmPhuongXa != null? this.emrDmPhuongXa.toDto() : null;
        dto.diaChi.dmQuanHuyen = this.emrDmQuanHuyen != null? this.emrDmQuanHuyen.toDto() : null;
        dto.diaChi.dmTinhThanh = this.emrDmTinhThanh != null? this.emrDmTinhThanh.toDto() : null;
        
        if(this.emrDmGioiTinh != null) {
            var maGioiTinh = this.emrDmGioiTinh.ma;
            var code = gioiTinhCodeMap.getOrDefault(maGioiTinh, "");
            var name = gioiTinhMap.getOrDefault(maGioiTinh , "");
            dto.dmGioiTinh = new DanhMuc(code, name, CodeSystemValue.GIOI_TINH);
        }
        
        //dto.soDienThoai = this.sodienthoainguoibaotin;
        dto.dmDanToc = this.emrDmDanToc != null? this.emrDmDanToc.toDto() : null;
        dto.dmTonGiao = this.emrDmTonGiao != null? this.emrDmDanToc.toDto() : null;
        dto.dmNgheNghiep = this.emrDmNgheNghiep != null? this.emrDmNgheNghiep.toDto() : null;
        dto.dmQuocTich = this.emrDmQuocGia != null? this.emrDmQuocGia.toDto() : null;
                
        return dto;
    }
    
    public BenhNhan toDto() {
        var dto = _toDto();
        dto.id = getFhirId();
        return dto;
    }
        
}
