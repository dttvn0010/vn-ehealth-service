package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkHoHap {

    public Boolean conngungtho;
    
    public Boolean thobung;

    public Boolean thonong;

    public Boolean thokhokhe;

    public Boolean thoritphequan;

    public Boolean thoritthanhquan;

    public Boolean tholomnguc;

    public Boolean thoranphoi;

    public String thoranphoichitiet;

    public String roiloan;

    public Integer silverman;

    public String longnguc;
    
    public String thetichkhi;
    
    public String tinhtrangbenhly;
}
