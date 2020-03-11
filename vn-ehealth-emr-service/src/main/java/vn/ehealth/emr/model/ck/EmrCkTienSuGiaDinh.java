package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTienSuGiaDinh {

    public Boolean sinhdoi;
    public Boolean didang;
    public Boolean benhditruyen;
    public Boolean daiduong;
    public Boolean caohuyetap;
    public String benhkhac;
}
