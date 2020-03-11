package vn.ehealth.emr.model.ck;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTinhTrangSanPhu {
    
    public Date ngayvooi;
    
    public String mauoi;
    
    public Boolean dethuong;
    
    public Boolean canthiep;
    
    public Date ngaycanthiep;
    
    public String lydo;
    
    public Date thoidiemvaode;
    public Date thoidiemmode;
    public String ngoithai;
    public Boolean kiemsoattucung;
    public String motakiemsoattucung;
    public String nhommau;
    public String paraDuthang;
    public String paraDenon;
    public String paraSay;
    public String paraSong;
    
    // Add moi 16/04/2015
    public String benhcuame;
    public String dieutribenhcuame;
    public String toantrang;
    public Integer mach;
    public String huyetap;
    public String nhietdo;
    public String motacachde;
    public String socon;
    public String sobenhanme;
    public String meNoinam;
    public String meSogiuong;
}
