package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTiemChung {

    public Boolean tclao;
    
    public Boolean tcbailiet;
    
    public Boolean tcsoi;
    
    public Boolean tchoga;
    
    public Boolean tcuonvan;
    
    public Boolean tcbachhau;
    
    public Boolean tckhac;
    
    public String tcmotakhac;
}
