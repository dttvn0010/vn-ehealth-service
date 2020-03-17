package vn.ehealth.emr.model.dto;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

public class DanhMuc {

    public String ma;
    public String ten;
    public String maNhom;
    public String phienBan;
    
    public DanhMuc() {
        
    }
    
    public DanhMuc(String ma, String name, String maNhom) {
        this.ma = ma;
        this.ten = name;
        this.maNhom = maNhom;
    }
    
    public DanhMuc(Coding code) {
        if(code == null) return;
        
        this.ma = code.getCode();
        this.ten = code.getDisplay();
        this.maNhom = code.getSystem();                
    }
    
    public DanhMuc(CodeableConcept concept) {
        if(concept == null) return;
        
        this.ten = concept.hasText()? concept.getText() : "";
        if(concept.hasCoding()) {
            var code = concept.getCodingFirstRep();
            this.ma = code.getCode();
            this.ten = code.getDisplay();
            this.maNhom = code.getSystem();
        }
    }
    
    public static DanhMuc fromConcept(CodeableConcept concept) {
        if(concept == null) return null;
        return new DanhMuc(concept);
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
