package vn.ehealth.auth.dto.request;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DanhMucDTO {
    
    public String ma = "";
    public String ten = "";
    public String maNhom;
    
    public static Coding toCoding(DanhMucDTO dto, String maNhom) {
        if(dto == null) return null;
        var obj = new Coding();
        obj.setCode(dto.ma);
        obj.setDisplay(dto.ten);
        obj.setSystem(maNhom != null? maNhom : dto.maNhom);
        return obj;
    }
    
    public static CodeableConcept toConcept(DanhMucDTO dto, String maNhom) {
        if(dto == null) return null;
        var obj = new CodeableConcept();
        obj.setText(dto.ten);
        obj.addCoding(toCoding(dto, maNhom));
        
        return obj;
    }
}
