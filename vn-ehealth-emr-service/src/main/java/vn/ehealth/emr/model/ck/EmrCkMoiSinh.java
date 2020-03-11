package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkMoiSinh {

    public String khuvucbenhcaptinh;
    public String khuvucsong;
    public String thoigiansong;
    public String moisinh;
    
    // Add moi 15/04/2015
    public Boolean benhdichTruonghoc;
    public Boolean treCungnha;
    public Boolean treGannha;
    public Boolean treCungtruong;
}
