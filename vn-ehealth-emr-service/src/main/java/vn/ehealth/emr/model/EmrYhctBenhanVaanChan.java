package vn.ehealth.emr.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctBenhanVaanChan {
   
    //public String lsthannhiet;
    //public String lsthannhietTen;
    //public String lsthannhietHienthi;
    public List<EmrDmContent> emrDmYhctThanNhiets;
    
    public String getLsthannhietHienthi( ) {
        if(emrDmYhctThanNhiets != null) {
            return emrDmYhctThanNhiets.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";         
    }
    
    //public String lstmohoi;
    //public String lstmohoiTen;
    //public String lstmohoiHienthi;
    public List<EmrDmContent> emrDmYhctMoHois;
    
    public String getLstmohoiHienthi( ) {
        if(emrDmYhctMoHois != null) {
            return emrDmYhctMoHois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";           
    }
    
    //public String lstdaumatco;    
    //public String lstdaumatcoTen;
    //public String lstdaumatcoHienthi;
    public List<EmrDmContent> emrDmYhctDauMatCos;
    
    public String getLstdaumatcoHienthi( ) {
        if(emrDmYhctDauMatCos != null) {
            return emrDmYhctDauMatCos.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";         
    }
    
    //public String lstchantay;
    //public String lstchantayTen;
    //public String lstchantayHienthi;
    public List<EmrDmContent> emrDmYhctChanTays;
    
    public String getLstchantay( ) {
        if(emrDmYhctChanTays != null) {
            return emrDmYhctChanTays.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";         
    }
    
    public String getLstchantayHienthi( ) {
        if(emrDmYhctChanTays != null) {
            return emrDmYhctChanTays.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";          
    }
    
    //public String lstlung;
    //public String lstlungTen;
    //public String lstlungHienthi;
    public List<EmrDmContent> emrDmYhctLungs;
    
    public String getLstlungHienthi( ) {
        if(emrDmYhctLungs != null) {
            return emrDmYhctLungs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    
    //public String lstbungnguc;
    //public String lstbungngucTen;
    //public String lstbungngucHienthi;
    public List<EmrDmContent> emrDmYhctBungNgucs;
    
    public String getLstbungngucHienthi( ) {
        if(emrDmYhctBungNgucs != null) {
            return emrDmYhctBungNgucs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lstan;
    //public String lstanTen;
    //public String lstanHienthi;
    public List<EmrDmContent> emrDmYhctAns;
    
    public String getLstanHienthi( ) {
        if(emrDmYhctAns != null) {
            return emrDmYhctAns.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lstuong;
    //public String lstuongTen;
    //public String lstuongHienthi;
    public List<EmrDmContent> emrDmYhctTuongs;
    
    public String getLstuongHienthi( ) {
        if(emrDmYhctTuongs != null) {
            return emrDmYhctTuongs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lstdaitien;
    //public String lstdaitienTen;
    //public String lstdaitienHienthi;
    public List<EmrDmContent> emrDmYhctDaiTiens;
    
    public String getLstdaitienHienthi( ) {
        if(emrDmYhctDaiTiens != null) {
            return emrDmYhctDaiTiens.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";         
    }
    
    //public String lsttieutien;
    //public String lsttieutienTen;
    //public String lsttieutienHienthi;
    public List<EmrDmContent> emrDmYhctTieuTiens;
    
    public String getLsttieutienHienthi( ) {
        if(emrDmYhctTieuTiens != null) {
            return emrDmYhctTieuTiens.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lstngu;
    //public String lstnguTen;
    //public String lstnguHienthi;
    public List<EmrDmContent> emrDmYhctNgus;
    
    public String getLstnguHienthi() {
        if(emrDmYhctNgus != null) {
            return emrDmYhctNgus.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstkinhnguyet;
    //public String lstkinhnguyetTen;
    //public String lstkinhnguyetHienthi;
    public List<EmrDmContent> emrDmYhctKinhNguyets;
    
    public String getLstkinhnguyetHienthi() {
        if(emrDmYhctKinhNguyets != null) {
            return emrDmYhctKinhNguyets.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstthongkinh;
    //public String lstthongkinhTen;
    //public String lstthongkinhHienthi;
    public List<EmrDmContent> emrDmYhctThongKinhs;
    
    public String getLstthongkinhHienthi() {
        if(emrDmYhctThongKinhs != null) {
            return emrDmYhctThongKinhs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstdoiha;
    //public String lstdoihaTen;
    //public String lstdoihaHienthi;
    public List<EmrDmContent> emrDmYhctDoiHas;
    
    public String getLstdoihaHienthi() {
        if(emrDmYhctDoiHas != null) {
            return emrDmYhctDoiHas.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstsinhduc;
    //public String lstsinhducTen;
    //public String lstsinhducHienthi;
    public List<EmrDmContent> emrDmYhctSinhDucs;
    
    public String getLstsinhducHienthi() {
        if(emrDmYhctSinhDucs != null) {
            return emrDmYhctSinhDucs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstsinhducnu;
    //public String lstsinhducnuTen;
    //public String lstsinhducnuHienthi;
    public List<EmrDmContent> emrDmYhctSinhDucNus;
    
    public String getLstsinhducnuHienthi() {
        if(emrDmYhctSinhDucNus != null) {
            return emrDmYhctSinhDucNus.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
        
    //public String lsthannhietbieuhien;
    //public String lsthannhietbieuhienTen;
    //public String lsthannhietbieuhienHienthi;
    public List<EmrDmContent> emrDmYhctThanNhietBieuHiens;
    
    public String getLsthannhietbieuhienHienthi() {
        if(emrDmYhctThanNhietBieuHiens != null) {
            return emrDmYhctThanNhietBieuHiens.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    public String motavaanchan;
    public String dkxuatvien;
}
