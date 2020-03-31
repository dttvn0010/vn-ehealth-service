package vn.ehealth.emr.model;

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
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createContactPoint;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createExtension;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createHumanName;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;

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
    
    public String sodienthoai;
    
    public String email;

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
        
        if(emrDmQuanHuyen != null) {
            obj.setDistrict(emrDmQuanHuyen.ten);
        }
        
        if(emrDmTinhThanh != null) {
            obj.setCity(emrDmTinhThanh.ten);
        }
        
        var tinhThanhExt = createExtension("city", EmrDmContent.toConcept(emrDmTinhThanh, CodeSystemValue.DVHC));        
        var quanHuyenExt = createExtension("district", EmrDmContent.toConcept(emrDmQuanHuyen, CodeSystemValue.DVHC));        
        var xaPhuongExt = createExtension("ward", EmrDmContent.toConcept(emrDmPhuongXa, CodeSystemValue.DVHC));
        
        var extension = obj.addExtension();
        extension.setUrl(ExtensionURL.DVHC);
        extension.addExtension(tinhThanhExt);
        extension.addExtension(quanHuyenExt);
        extension.addExtension(xaPhuongExt);
        return obj;
    }
   
    @JsonIgnore
    public Patient getPatientInDB() {
        var params = mapOf("active", true, 
                            "identifier.system", IdentifierSystem.DINH_DANH_Y_TE,
                            "identifier.value", sobhyt
                        );
        
        var criteria = MongoUtils.createCriteria(params);
        var lst = DaoFactory.getPatientDao().findByCriteria(criteria);
        return lst.size() > 0 ? lst.get(0) : null;
    }
    
    @JsonIgnore
    public Patient toFhir() {
        
        var obj = new Patient();
        obj.setName(listOf(createHumanName(tendaydu)));        
        obj.setBirthDate(ngaysinh);
        
        if(!StringUtils.isBlank(sobhyt)) {
            var mohIdentifier = createIdentifier(sobhyt, IdentifierSystem.DINH_DANH_Y_TE, 
                                                    null, ngayhethanthebhyt);
            obj.addIdentifier(mohIdentifier);
        }
        
        if(!StringUtils.isBlank(iddinhdanhphu)) {
            var nationalIdentifier = createIdentifier(iddinhdanhphu, IdentifierSystem.CMND);
            obj.addIdentifier(nationalIdentifier);
        }
        
        
        if(emrDmGioiTinh != null) {
            var genderCode = gioiTinhCodeMap.get(emrDmGioiTinh.ma);
            obj.setGender(AdministrativeGender.fromCode(genderCode));
        }
        
        obj.setAddress(listOf(getAddress()));
        if(!StringUtils.isBlank(sodienthoai)) {
            obj.addTelecom(createContactPoint(sodienthoai, ContactPointSystem.PHONE));
        }
        
        if(!StringUtils.isBlank(email)) {
            obj.addTelecom(createContactPoint(email, ContactPointSystem.EMAIL));
        }

        var danTocExt = createExtension(ExtensionURL.DAN_TOC, EmrDmContent.toConcept(emrDmDanToc, CodeSystemValue.DAN_TOC));
        var tonGiaoExt = createExtension(ExtensionURL.TON_GIAO, EmrDmContent.toConcept(emrDmTonGiao, CodeSystemValue.TON_GIAO));
        var ngheNghiepExt = createExtension(ExtensionURL.NGHE_NGHIEP, EmrDmContent.toConcept(emrDmNgheNghiep, CodeSystemValue.NGHE_NGHIEP));
        var quocTichExt = createExtension(ExtensionURL.QUOC_TICH, EmrDmContent.toConcept(emrDmQuocGia, CodeSystemValue.QUOC_GIA));
        
        obj.setExtension(listOf(danTocExt, tonGiaoExt, ngheNghiepExt));
        obj.setModifierExtension(listOf(quocTichExt));
        
        return obj;
    }    
        
}
