package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkKhamSoSinh {

    public Boolean ditatbamsinh;
    public Boolean ditathaumon;
    public String motaditat;
    public String tinhhinhsosinhvaokhoa;
    public String tinhtrangtoanthan;
    public Integer mausacda;
    public String tinhtranghohap;
    public Integer nhiptho;
    public Integer silverman;
    public Integer nhiptim;
}
