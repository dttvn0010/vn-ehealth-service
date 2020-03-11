package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTinhTrangSoSinh {
    
    public Boolean sskhocngay;
    
    public Boolean ssngat;
    
    public Boolean sskhac;
    
    public String nguoidode;
    
    public Integer apgar01;
    
    public Integer apgar05;
    
    public Integer apgar10;
    
    public Double cannang;
    
    public String tinhtrangdinhduong;
    
    public String tennguoichuyensosinh;
    
    public Integer tuoi;
}
