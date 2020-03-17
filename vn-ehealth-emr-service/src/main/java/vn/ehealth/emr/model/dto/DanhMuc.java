package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;

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
    
    public DanhMuc(BaseCoding code) {
        if(code == null) return;
        
        this.ma = code.code;
        this.ten = code.display;
        this.maNhom = code.system;                
    }
    
    public DanhMuc(BaseCodeableConcept codeConcept) {
        if(codeConcept == null) return;
        
        this.ten = codeConcept.text;
        if(codeConcept.coding != null && codeConcept.coding.size() > 0) {
            var code = codeConcept.coding.get(0);
            this.ma = code.code;
            this.ten = code.display;
            this.maNhom = code.system;
        }
    }
    
    public static BaseCoding toBaseCoding(DanhMuc dto, String maNhom) {
        if(dto == null) return null;
        var ent = new BaseCoding();
        ent.code = dto.ma;
        ent.display = dto.ten;
        ent.system = maNhom != null? maNhom : dto.maNhom;
        return ent;
    }
    
    public static BaseCodeableConcept toBaseCodeableConcept(DanhMuc dto, String maNhom) {
        if(dto == null) return null;
        var ent = new BaseCodeableConcept();
        ent.text = dto.ten;
        
        ent.coding = new ArrayList<>();
        var code = toBaseCoding(dto, maNhom);
        if(code != null) ent.coding.add(code);
        
        return ent;
    }
}
