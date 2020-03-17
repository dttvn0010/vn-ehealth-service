package vn.ehealth.emr.model.dto;

import java.util.Optional;

import org.hl7.fhir.r4.model.Address;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;

public class DiaChi {

    public String diaChiChiTiet;
    public DanhMuc dmXaPhuong;
    public DanhMuc dmQuanHuyen;
    public DanhMuc dmTinhThanh;
    public DanhMuc dmQuocGia;
    
    public static DiaChi fromFhirModel(Address obj) {
        if(obj == null) return null;
        var dto = new DiaChi();
        
        if(obj.getLine() != null && obj.getLine().size() > 0) {
            dto.diaChiChiTiet = obj.getLine().get(0).getValue();
        }
        
        if(obj.getLine() != null && obj.getLine().size() > 1) {
            String xaPhuong = obj.getLine().get(1).getValue();
            dto.dmXaPhuong = new DanhMuc("", xaPhuong, CodeSystemValue.DVHC_XA_PHUONG);
        }
        
        if(obj.hasDistrict()) {
            dto.dmQuanHuyen = new DanhMuc("", obj.getDistrict(), CodeSystemValue.DVHC_QUAN_HUYEN);
        }
        
        dto.dmQuanHuyen = new DanhMuc("", obj.getDistrict(), CodeSystemValue.DVHC_QUAN_HUYEN);
        dto.dmTinhThanh = new DanhMuc("", obj.getCity(), CodeSystemValue.DVHC_TINH_THANH);
        dto.dmQuocGia = new DanhMuc("", obj.getCountry(), CodeSystemValue.DVHC_QUOC_GIA);
        return dto;
    }
    
    public static Address toFhirModel(DiaChi dto) {
        if(dto == null) return null;
        var obj = new Address();
        obj.addLine(dto.diaChiChiTiet != null? dto.diaChiChiTiet : "");
        obj.addLine(Optional.ofNullable(dto.dmXaPhuong).map(x -> x.ten).orElse(""));
        obj.setDistrict(Optional.ofNullable(dto.dmQuanHuyen).map(x -> x.ten).orElse(""));
        obj.setCity(Optional.ofNullable(dto.dmTinhThanh).map(x -> x.ten).orElse(""));
        obj.setCountry(Optional.ofNullable(dto.dmQuocGia).map(x -> x.ten).orElse(""));
        return obj;
    }
}
