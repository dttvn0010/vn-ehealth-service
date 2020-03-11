package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkPhuongPhapHoiSinh {

    public Boolean hutdich;
    
    public Boolean xoaboptim;
    
    public Boolean thooxy;
    
    public Boolean noikhiquan;
    
    public Boolean bopbongoxy;
    
    public Boolean khac;

    public String mota;
}
