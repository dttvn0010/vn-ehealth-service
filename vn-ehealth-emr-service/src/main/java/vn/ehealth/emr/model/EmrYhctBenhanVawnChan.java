package vn.ehealth.emr.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctBenhanVawnChan {

    //public String lsttiengnoi;
    //public String lsttiengnoiTen;
    //public String lsttiengnoiHienthi;
    public List<EmrDmContent> emrDmYhctTiengNois;
    
    public String getLsttiengnoiHienthi() {        
        if(emrDmYhctTiengNois != null) {
            return emrDmYhctTiengNois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lsthoitho;
    //public String lsthoithoTen;
    //public String lsthoithoHienthi;
    public List<EmrDmContent> emrDmYhctHoiThos;
    
    public String getLsthoithoHienthi() {
        if(emrDmYhctHoiThos != null) {
            return emrDmYhctHoiThos.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lsttiengho;
    //public String lsttienghoTen;
    //public String lsttienghoHienthi;
    public List<EmrDmContent> emrDmYhctTiengHos;
    
    public String getLsttienghoTen() {
        if(emrDmYhctTiengHos != null) {
            return emrDmYhctTiengHos.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    public String getLsttienghoHienthi() {
        if(emrDmYhctTiengHos != null) {
            return emrDmYhctTiengHos.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";        
    }
    
    //public String lstmuicothe;
    //public String lstmuicotheTen;
    //public String lstmuicotheHienthi;
    public List<EmrDmContent> emrDmYhctMuiCoThes;
    
    public String getLstmuicotheHienthi() {
        if(emrDmYhctMuiCoThes != null) {
            return emrDmYhctMuiCoThes.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstchatthaibenhly;
    //public String lstchatthaibenhlyTen;
    //public String lstchatthaibenhlyHienthi;
    public List<EmrDmContent> emrDmYhctChatThaiBenhLys;
    
    public String getLstchatthaibenhlyHienthi() {
        if(emrDmYhctChatThaiBenhLys != null) {
            return emrDmYhctChatThaiBenhLys.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    public String amthanho;
    public String amthanhnac;
    public String motavawnchan;
}
