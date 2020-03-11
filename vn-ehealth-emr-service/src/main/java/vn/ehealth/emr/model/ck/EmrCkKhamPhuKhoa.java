package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkKhamPhuKhoa {

    public String dauhieusinhducthuphat;
    public String moilon;
    public String moibe;
    public String amvat;
    public String amho;
    public String mangtrinh;
    public String tangsinhmon;
    public String amdao;
    public String cotucung;
    public String thantucung;
    public String phanphu;
    public String cactuicung;
    public String thongtinkhac;
}
