package vn.ehealth.emr.model.ck;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkSkTinhTrangSanPhu {

    public Integer tinhthan;
    public Boolean phu;
    public String proteinnieu;
    public Boolean seomocu;
    public Boolean dauvetmo;
    public Double chieucaotucung;
    public Integer timthai;
    public Integer tinhtrangtimthai;
    public Integer cotucung;
    public Integer domo;
    public Integer chisobishop;
    public Integer dauoi;
    public Date thoidiemvooi;
    public Integer maunuocoi;
    
    // Add moi 16/04/2015
    public String nhietdo;
    public String mach;
    public String huyetap;
    public String nhiptho;
    public String vongbung;
    public String ngoithai;
    public String concotucung;
}
