package vn.ehealth.auth.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createContactPoint;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createExtension;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createHumanName;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Person;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.auth.dto.request.DanhMucDTO;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_person")
public class EmrPerson {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }
    
    public String tenDayDu;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysinh;
    public String email;
    public String sodienthoai;
    public String cmnd;
    public String noilamviec;
    public String diachi;

    public DanhMucDTO dmGioiTinh;
    public DanhMucDTO dmDanToc;
    public DanhMucDTO dmTonGiao;
    public DanhMucDTO dmQuocGia;
    public DanhMucDTO dmNgheNghiep;
    public DanhMucDTO dmPhuongXa;
    public DanhMucDTO dmQuanHuyen;
    public DanhMucDTO dmTinhThanh;
    
    public ObjectId userId;
    
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
        
        var tinhThanhExt = createExtension("city", DanhMucDTO.toConcept(dmTinhThanh, CodeSystemValue.DVHC));        
        var quanHuyenExt = createExtension("district", DanhMucDTO.toConcept(dmQuanHuyen, CodeSystemValue.DVHC));        
        var xaPhuongExt = createExtension("ward", DanhMucDTO.toConcept(dmPhuongXa, CodeSystemValue.DVHC));
        
        var extension = obj.addExtension();
        extension.setUrl(ExtensionURL.DVHC);
        extension.addExtension(tinhThanhExt);
        extension.addExtension(quanHuyenExt);
        extension.addExtension(xaPhuongExt);
        return obj;
    }
    
    @JsonIgnore
    public Person toFhir() {
        
        var obj = new Person();
        obj.setName(listOf(createHumanName(tenDayDu)));        
        obj.setBirthDate(ngaysinh);
        
        if(!StringUtils.isBlank(cmnd)) {
            var nationalIdentifier = createIdentifier(cmnd, IdentifierSystem.CMND);
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

        var danTocExt = createExtension(ExtensionURL.DAN_TOC, DanhMucDTO.toConcept(dmDanToc, CodeSystemValue.DAN_TOC));
        var tonGiaoExt = createExtension(ExtensionURL.TON_GIAO, DanhMucDTO.toConcept(dmTonGiao, CodeSystemValue.TON_GIAO));
        var ngheNghiepExt = createExtension(ExtensionURL.NGHE_NGHIEP, DanhMucDTO.toConcept(dmNgheNghiep, CodeSystemValue.NGHE_NGHIEP));
        var quocTichExt = createExtension(ExtensionURL.QUOC_TICH, DanhMucDTO.toConcept(dmQuocGia, CodeSystemValue.QUOC_GIA));
        
        obj.setExtension(listOf(danTocExt, tonGiaoExt, ngheNghiepExt));
        obj.setModifierExtension(listOf(quocTichExt));
        
        return obj;
    } 
    
    public EmrPerson() {
        
    }
    
    public EmrPerson(Person person) {
        
    }
}
