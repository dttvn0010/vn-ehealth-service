package vn.ehealth.emr.model.ck;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTienSuSanKhoa {
    public String thoigiandiadiem;
    public String tuoithai;
    public String dienbienthai;
    public String cachde;
    public String tresosinh;
    public String hausan;
    
    public String paraDuthang;
    public String paraDenon;
    public String paraSay;
    public String paraSong;
    
    public List<EmrCkTienSuSanKhoaChiTiet> emrCkTienSuSanKhoaChiTiets = new ArrayList<>();

    //add SonVT 08042016
    public Integer solancothai;

}
