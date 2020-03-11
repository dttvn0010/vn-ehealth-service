package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTuanHoan {

    public Boolean timro;
    
    public Boolean timmo;
    
    public Boolean timgallop;
    
    public Boolean timamthoi;
    
    public String timchitiet;
    
    public Boolean tinhmachconoi;
    
    public Integer thoigiandaymaomach;
    
    public Boolean vamohoi;
    
    public Boolean danoibong;
    
    public String roiloantim;
    
    // Add moi 13/04/2015
    public String nhiptim;
}
