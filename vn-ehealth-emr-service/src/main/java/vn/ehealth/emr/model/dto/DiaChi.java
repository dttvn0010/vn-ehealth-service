package vn.ehealth.emr.model.dto;

import java.util.Optional;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.ExtensionURL;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class DiaChi {

    public String diaChiChiTiet;
    public DanhMuc dmXaPhuong;
    public DanhMuc dmQuanHuyen;
    public DanhMuc dmTinhThanh;
    
    public static DiaChi fromFhirModel(Address obj) {
        if(obj == null) return null;
        var dto = new DiaChi();
        
        if(obj.getLine() != null && obj.getLine().size() > 0) {
            dto.diaChiChiTiet = obj.getText();
        }
        
        if(obj.hasExtension()) {
            var ext = findExtensionByURL(obj.getExtension(), ExtensionURL.DVHC + "/city");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.dmTinhThanh = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
            
            ext = findExtensionByURL(obj.getExtension(), ExtensionURL.DVHC + "/district");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.dmQuanHuyen = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
            
            ext = findExtensionByURL(obj.getExtension(), ExtensionURL.DVHC + "/ward");
            if(ext != null && ext.getValue() instanceof CodeableConcept) {
                dto.dmXaPhuong = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
            }
        }
        return dto;
    }
    
    public static Address toFhirModel(DiaChi dto) {
        if(dto == null) return null;
        var obj = new Address();
        obj.setText(dto.diaChiChiTiet != null? dto.diaChiChiTiet : "");
        obj.addLine(dto.diaChiChiTiet != null? dto.diaChiChiTiet : "");        
        obj.setDistrict(Optional.ofNullable(dto.dmQuanHuyen).map(x -> x.ten).orElse(""));
        obj.setCity(Optional.ofNullable(dto.dmTinhThanh).map(x -> x.ten).orElse(""));
        
        var tinhThanhExt = createExtension(ExtensionURL.DVHC + "/city", 
                DanhMuc.toConcept(dto.dmTinhThanh, CodeSystemValue.DVHC));
        
        var quanHuyenExt = createExtension(ExtensionURL.DVHC + "/district", 
                DanhMuc.toConcept(dto.dmQuanHuyen, CodeSystemValue.DVHC));
        
        var xaPhuongExt = createExtension(ExtensionURL.DVHC + "/ward", 
                DanhMuc.toConcept(dto.dmXaPhuong, CodeSystemValue.DVHC));
        
        obj.setExtension(listOf(tinhThanhExt, quanHuyenExt, xaPhuongExt));
        
        return obj;
    }
}
