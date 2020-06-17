package vn.ehealth.cdr.model.component;

import java.util.Map;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class DanhMuc {
    
    public String ma = "";
    public String ten = "";
    
    @JsonIgnore
    public String maicd = "";
    
    @JsonIgnore
    public String code = "";
    
    @JsonIgnore
    public String codeSystem = "";
    
    public Map<String, Object> extra;
    
    @JsonIgnore
    public String maNhom;
    
    @JsonIgnore
    public String phienBan;
    
    public DanhMuc() {
        
    }
    
    public DanhMuc(String ma, String ten) {
        this.ma = ma;
        this.ten = ten;
    }
    
    public static Coding toCoding(DanhMuc dto, String maNhom) {
        if(dto == null) return null;
        var obj = new Coding();
        obj.setCode(dto.ma);
        obj.setDisplay(dto.ten);
        obj.setSystem(maNhom != null? maNhom : dto.maNhom);
        return obj;
    }
    
    public static CodeableConcept toConcept(DanhMuc dto, String maNhom) {
        if(dto == null) return null;
        var obj = new CodeableConcept();
        obj.setText(dto.ten);
        obj.addCoding(toCoding(dto, maNhom));
        
        return obj;
    }
}
