package vn.ehealth.emr.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctBenhanVongChan {

    //public String lsthinhthai;
    //public String lsthinhthaiTen;
    //public String lsthinhthaiHienthi;
    public List<EmrDmContent> emrDmYhctHinhThais;
    
    public String getLsthinhthaiHienthi() {
        if(emrDmYhctHinhThais != null) {
            return emrDmYhctHinhThais.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstthan;
    //public String lstthanTen;
    //public String lstthanHienthi;
    public List<EmrDmContent> emrDmYhctThans;
    
    public String getLstthanHienthi() {
        if(emrDmYhctThans != null) {
            return emrDmYhctThans.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstsac;
    //public String lstsacTen;
    //public String lstsacHienthi;
    public List<EmrDmContent> emrDmYhctSacs;
    
    public String getLstsacHienthi() {
        if(emrDmYhctSacs != null) {
            return emrDmYhctSacs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lsttrach;
    //public String lsttrachTen;
    //public String lsttrachHienthi;
    public List<EmrDmContent> emrDmYhctTrachs;
    
    public String getLsttrachHienthi() {
        if(emrDmYhctTrachs != null) {
            return emrDmYhctTrachs.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstchatluoi;
    //public String lstchatluoiTen;
    //public String lstchatluoiHienthi;
    public List<EmrDmContent> emrDmYhctChatLuois;
    
    public String getLstchatluoiHienthi() {
        if(emrDmYhctChatLuois != null) {
            return emrDmYhctChatLuois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstsacluoi;
    //public String lstsacluoiTen;
    //public String lstsacluoiHienthi;
    public List<EmrDmContent> emrDmYhctSacLuois;
    
    public String getLstsacluoiHienthi() {
        if(emrDmYhctSacLuois != null) {
            return emrDmYhctSacLuois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    //public String lstreuluoi;
    //public String lstreuluoiTen;
    //public String lstreuluoiHienthi;
    public List<EmrDmContent> emrDmYhctReuLuois;
    
    public String getLstreuluoiHienthi() {
        if(emrDmYhctReuLuois != null) {
            return emrDmYhctReuLuois.stream().map(x -> x.ten).collect(Collectors.joining(","));
        }
        return "";
    }
    
    public String motavongchan;    
    
}
