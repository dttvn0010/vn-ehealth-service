package vn.ehealth.emr.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrThanhVienPttt {

    //public Integer idvaitro;
    
    public EmrDmContent emrDmVaiTro;
    
    public String tenbacsi;
}
