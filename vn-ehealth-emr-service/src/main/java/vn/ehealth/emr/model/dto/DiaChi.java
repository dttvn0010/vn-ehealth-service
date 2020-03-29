package vn.ehealth.emr.model.dto;

import java.util.Optional;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CodeableConcept;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class DiaChi {

    public String diaChiChiTiet;
    public DanhMuc dmXaPhuong;
    public DanhMuc dmQuanHuyen;
    public DanhMuc dmTinhThanh;
    
    public static DiaChi fromFhirModel(Address obj) {
        if(obj == null) return null;
        var dto = new DiaChi();
        
        dto.diaChiChiTiet = obj.getText();
        
        if(obj.hasExtension()) {
            var extension = obj.getExtensionFirstRep();
            if(extension.hasExtension()) {            
                var ext = findExtensionByURL(extension.getExtension(), "city");
                if(ext != null && ext.getValue() instanceof CodeableConcept) {
                    dto.dmTinhThanh = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
                }
                
                ext = findExtensionByURL(extension.getExtension(), "district");
                if(ext != null && ext.getValue() instanceof CodeableConcept) {
                    dto.dmQuanHuyen = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
                }
                
                ext = findExtensionByURL(extension.getExtension(), "ward");
                if(ext != null && ext.getValue() instanceof CodeableConcept) {
                    dto.dmXaPhuong = DanhMuc.fromConcept((CodeableConcept) ext.getValue());
                }
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
        
        var tinhThanhExt = createExtension("city", DanhMuc.toConcept(dto.dmTinhThanh, CodeSystemValue.DVHC));        
        var quanHuyenExt = createExtension("district", DanhMuc.toConcept(dto.dmQuanHuyen, CodeSystemValue.DVHC));        
        var xaPhuongExt = createExtension("ward", DanhMuc.toConcept(dto.dmXaPhuong, CodeSystemValue.DVHC));
        
        var extension = obj.addExtension();
        extension.setUrl(ExtensionURL.DVHC);
        extension.addExtension(tinhThanhExt);
        extension.addExtension(quanHuyenExt);
        extension.addExtension(xaPhuongExt);
        
        return obj;
    }
}
