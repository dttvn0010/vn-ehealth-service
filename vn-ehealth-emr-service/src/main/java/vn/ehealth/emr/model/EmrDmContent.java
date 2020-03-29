package vn.ehealth.emr.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.model.dto.DanhMuc;

@JsonInclude(Include.NON_NULL)
public class EmrDmContent {
    
    public String ma = "";
    public String ten = "";
    
    @JsonIgnore
    public String maicd = "";
    
    @JsonIgnore
    public String code = "";
    
    @JsonIgnore
    public String codeSystem = "";
    
    public Map<String, Object> extension;
    
    public String maNhom;
    public String phienBan;
    
    public DanhMuc toDto() {
        var dto = new DanhMuc();
        dto.ma = ma;
        dto.ten = ten;
        dto.maNhom = maNhom;
        dto.phienBan = phienBan;
        return dto;
    }
}
