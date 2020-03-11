package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTienSuBanThanSanKhoa {

    public Boolean caohuyetap;
    public Boolean benhtim;
    public Boolean laophoi;
    public Boolean henphequan;
    public Boolean benhthan;
    public Boolean basedow;
    public Boolean viemtactinhmach;
    public Boolean dongkinh;
    public Boolean moruotthua;
    public Boolean diungthuoc;
    public String thongtincuthe;
    public String phauthuatobung;
}
