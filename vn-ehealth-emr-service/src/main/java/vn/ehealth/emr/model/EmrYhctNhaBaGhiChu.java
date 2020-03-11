package vn.ehealth.emr.model;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctNhaBaGhiChu {
    
    public Date ngayhgiohen;
    public String ghichu;
}
