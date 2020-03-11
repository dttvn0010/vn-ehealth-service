package vn.ehealth.emr.model.ck;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTienSuSanKhoaChiTiet {
    
    public int lancothai;
    public Integer nam;
    public Boolean duthang;
    public Boolean denon;
    public Boolean say;
    public Boolean song;
    public Boolean hut;
    public Boolean nao;
    public Boolean covac;
    public Boolean chuangoaitucung;
    public Boolean chuatrung;
    public Boolean thaichetluu;
    public Double cannang;
    public String phuongphapde;
    public String taibien;
    
    //ngay 23/7/2015
    public String dienbien;
    public String hausan;
    public Date ngayketthucthainghen;
    public Integer tuoithai;
    
    public int getLancothai() {
        return lancothai;
    }
    
    public Integer getNam() {
        return nam;
    }
    
    public Boolean getDuthang() {
        return duthang;
    }
    
    public Boolean getDenon() {
        return denon;
    }
    
    public Boolean getSay() {
        return say;
    }
    
    public Boolean getSong() {
        return song;
    }
    
    public Boolean getHut() {
        return hut;
    }
    
    public Boolean getNao() {
        return nao;
    }
    
    public Boolean getCovac() {
        return covac;
    }
    
    public Boolean getChuangoaitucung() {
        return chuangoaitucung;
    }
    
    public Boolean getChuatrung() {
        return chuatrung;
    }
    
    public Boolean getThaichetluu() {
        return thaichetluu;
    }
    
    public Double getCannang() {
        return cannang;
    }
    
    public String getPhuongphapde() {
        return phuongphapde;
    }
    
    public String getTaibien() {
        return taibien;
    }
    
    public String getDienbien() {
        return dienbien;
    }
    
    public String getHausan() {
        return hausan;
    }
    
    public Date getNgayketthucthainghen() {
        return ngayketthucthainghen;
    }
    
    public Integer getTuoithai() {
        return tuoithai;
    }    
}
