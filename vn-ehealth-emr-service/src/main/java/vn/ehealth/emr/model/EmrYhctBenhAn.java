package vn.ehealth.emr.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctBenhAn {

    public EmrDmContent emrDmYhctCheDoChamSoc;
    
    public String tomtattuchan;
    
    public String luantri;
    
    public String phapdieutri;
    
    public String phuongthuoc;
    
    public String phuonghuyet;
    
    public String phuongphapkhac;
    
    public String tienluong;
    
    // public String lstchedodinhduong;
    // public String lstchedodinhduongTen;
    // public String lstchedodinhduongHienthi;
        
    public List<EmrDmContent> emrDmYhctCheDoDinhDuongs;
    
    public String getLstchedodinhduongHienthi() {
        if(emrDmYhctCheDoDinhDuongs != null) {
            return emrDmYhctCheDoDinhDuongs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    public String motachedodinhduong;
    
    public String motachamsoc;
    
    public String dieutriXoabopbamhuyet;
    
    public String dieutriKethopyhhd;
    
    public EmrYhctBenhanVaanChan emrYhctBenhanVaanChan;
    
    public EmrYhctBenhanThietChan emrYhctBenhanThietChan;
    
    public EmrYhctBenhanVongChan emrYhctBenhanVongChan;
    
    public EmrYhctBenhanVawnChan emrYhctBenhanVawnChan;
        
    
}
