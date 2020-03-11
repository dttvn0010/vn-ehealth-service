package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrQuaTrinhSuDungThuoc {
    
    public EmrDmContent emrDmThuoc;
    
    public String mathuoc;

    //public String phienbandmthuoc;

    public String cachdung;

    public Date ngaysudungthuoc;

    public Integer soluong;

}
