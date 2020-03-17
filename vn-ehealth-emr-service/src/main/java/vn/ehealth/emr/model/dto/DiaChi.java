package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseAddress;

public class DiaChi {

    public String diaChiChiTiet;
    public DanhMuc dmXaPhuong;
    public DanhMuc dmQuanHuyen;
    public DanhMuc dmTinhThanh;
    public DanhMuc dmQuocGia;
    
    public static DiaChi fromEntity(BaseAddress ent) {
        if(ent == null) return null;
        var dto = new DiaChi();
        dto.diaChiChiTiet = ent.addressLine1;
        dto.dmXaPhuong = new DanhMuc("", ent.addressLine2, CodeSystem.DVHC_XA_PHUONG);
        dto.dmQuanHuyen = new DanhMuc("", ent.district, CodeSystem.DVHC_QUAN_HUYEN);
        dto.dmTinhThanh = new DanhMuc("", ent.city, CodeSystem.DVHC_TINH_THANH);
        dto.dmQuocGia = new DanhMuc("", ent.country, CodeSystem.DVHC_QUOC_GIA);
        return dto;
    }
    
    public static BaseAddress toEntity(DiaChi dto) {
        if(dto == null) return null;
        var ent = new BaseAddress();
        ent.addressLine1 = dto.diaChiChiTiet != null? dto.diaChiChiTiet : "";
        ent.addressLine2 = dto.dmXaPhuong != null? dto.dmXaPhuong.ten : "";
        ent.district = dto.dmQuanHuyen != null? dto.dmQuanHuyen.ten : "";
        ent.city = dto.dmTinhThanh != null? dto.dmTinhThanh.ten : "";
        ent.country = dto.dmQuocGia != null? dto.dmQuocGia.ten : "";
        return ent;
    }
}
