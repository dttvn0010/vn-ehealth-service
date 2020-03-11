package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrYhctNhaBa {
    
    public Date ngaybatdauchuaNhaba;
    public String tenbacsichuaNhaba;
    public String chandoanNhaba;
}
