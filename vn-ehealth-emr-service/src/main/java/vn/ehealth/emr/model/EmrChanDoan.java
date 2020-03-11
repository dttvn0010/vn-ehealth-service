package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrChanDoan {

    public EmrDmContent emrDmMaBenhChandoandieutri;
    
    public EmrDmContent emrDmMaBenhChandoankkb;
        
    public List<EmrDmContent> emrDmMaBenhChandoantruocpts = new ArrayList<>();
    public EmrDmContent emrDmMaBenhChandoantruocpt;
    
    public List<EmrDmContent> emrDmMaBenhChandoansaupts = new ArrayList<>();
    public EmrDmContent emrDmMaBenhChandoansaupt;
        
    public EmrDmContent emrDmMaBenhChandoannoiden;
    
    public EmrDmContent emrDmMaBenhChandoanraviennguyennhan;
    
    public EmrDmContent emrDmMaBenhChandoanravienchinh;    
    
    public List<EmrDmContent> emrDmMaBenhChandoanravienkemtheos = new ArrayList<>();    
    public EmrDmContent emrDmMaBenhChandoanravienkemtheo;
    
    public EmrDmContent emrDmLyDoTaiBienBienChung;
    
    public String motachandoannoiden;

    public String motachandoankkb;

    public String motachandoandieutri;

    public String motachandoanravienchinh;

    public String motachandoanraviennguyennhan;
    
    public String motachandoanravienkemtheo;
    
    public String motachandoantruocpt;

    public String motachandoansaupt;

    public Boolean bitaibien;

    public Boolean bibienchung;

    public Integer tongsongaysaupt;

    public Integer tongsolanpt;
    
    public EmrDmContent getEmrDmMaBenhChandoanravienkemtheo() {
        if(emrDmMaBenhChandoanravienkemtheos != null && emrDmMaBenhChandoanravienkemtheos.size() > 0) {
            return emrDmMaBenhChandoanravienkemtheos.get(0);
        }
        return null;
    }
    
    public EmrDmContent getEmrDmMaBenhChandoantruocpt() {
        if(emrDmMaBenhChandoantruocpts != null && emrDmMaBenhChandoantruocpts.size() > 0) {
            return emrDmMaBenhChandoantruocpts.get(0);
        }
        
        return null;        
    }
    
    public EmrDmContent getEmrDmMaBenhChandoansaupt() {
        if(emrDmMaBenhChandoansaupts != null && emrDmMaBenhChandoansaupts.size() > 0) {
            return emrDmMaBenhChandoansaupts.get(0);
        }
        
        return null;
    }

}
