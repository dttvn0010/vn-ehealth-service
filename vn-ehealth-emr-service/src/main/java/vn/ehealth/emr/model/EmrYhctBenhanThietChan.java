package vn.ehealth.emr.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctBenhanThietChan {

    //public String lstxucchan;
    //public String lstxucchanTen;
    //public String lstxucchanHienthi;
    public List<EmrDmContent> emrDmYhctXucChans;
    
    public String getLstxucchanHienthi() {
        if(emrDmYhctXucChans != null) {
            return emrDmYhctXucChans.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstconhuc;
    //public String lstconhucTen;
    //public String lstconhucHienthi;
    public List<EmrDmContent> emrDmYhctCoNhucs;
    
    public String getLstconhucHienthi() {
        if(emrDmYhctCoNhucs != null) {
            return emrDmYhctCoNhucs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstphucchan;
    //public String lstphucchanTen;
    //public String lstphucchanHienthi;
    public List<EmrDmContent> emrDmYhctPhucChans;
    
    public String getLstphucchanHienthi() {
        if(emrDmYhctPhucChans != null) {
            return emrDmYhctPhucChans.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchantongquat;
    //public String lstmachchantongquatTen;
    //public String lstmachchantongquatHienthi;
    public List<EmrDmContent> emrDmYhctMachChanTongQuats;
    
    public String getLstmachchantongquatHienthi() {
        if(emrDmYhctMachChanTongQuats != null) {
            return emrDmYhctMachChanTongQuats.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchanThonTaytrai;
    //public String lstmachchanThonTaytraiTen;
    //public String lstmachchanThonTaytraiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanThonTayTrais;
    
    public String getLstmachchanThonTaytraiHienthi() {
        if(emrDmYhctMachChanThonTayTrais != null) {
            return emrDmYhctMachChanThonTayTrais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchanQuanTaytrai;
    //public String lstmachchanQuanTaytraiTen;
    //public String lstmachchanQuanTaytraiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanQuanTayTrais;
    
    public String getLstmachchanQuanTaytraiHienthi() {
        if(emrDmYhctMachChanQuanTayTrais != null) {
            return emrDmYhctMachChanQuanTayTrais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchanXichTaytrai;
    //public String lstmachchanXichTaytraiTen;    
    //public String lstmachchanXichTaytraiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanXichTayTrais;
    
    public String getLstmachchanXichTaytraiHienthi() {
        if(emrDmYhctMachChanXichTayTrais != null) {
            return emrDmYhctMachChanXichTayTrais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchanThonTayphai;
    //public String lstmachchanThonTayphaiTen;
    //public String lstmachchanThonTayphaiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanThonTayPhais;
    
    public String getLstmachchanThonTayphaiHienthi() {
        if(emrDmYhctMachChanThonTayPhais != null) {
            return emrDmYhctMachChanThonTayPhais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstmachchanQuanTayphai;
    //public String lstmachchanQuanTayphaiTen;
    //public String lstmachchanQuanTayphaiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanQuanTayPhais;
    
    public String getLstmachchanQuanTayphaiHienthi() {
        if(emrDmYhctMachChanQuanTayPhais != null) {
            return emrDmYhctMachChanQuanTayPhais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return ""; 
    }
    
    //public String lstmachchanXichTayphai;
    //public String lstmachchanXichTayphaiTen;
    //public String lstmachchanXichTayphaiHienthi;
    public List<EmrDmContent> emrDmYhctMachChanXichTayPhais;
    
    public String getLstmachchanXichTayphaiHienthi() {        
        if(emrDmYhctMachChanXichTayPhais != null) {
            return emrDmYhctMachChanXichTayPhais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
            
    //public String lstxucchanmohoi;
    //public String lstxucchanmohoiTen;
    //public String lstxucchanmohoiHienthi;
    public List<EmrDmContent> emrDmYhctXucChanMoHois;
    
    public String getLstxucchanmohoiHienthi() {
        if(emrDmYhctXucChanMoHois != null) {
            return emrDmYhctXucChanMoHois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
        
    public String motamachchanTaytrai;
    public String motamachchanTayphai;
    public String motaxucchan;    
    public String motathietchan;
    
}
