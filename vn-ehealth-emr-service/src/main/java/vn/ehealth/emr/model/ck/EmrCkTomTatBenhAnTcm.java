package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTomTatBenhAnTcm {

    public Integer songaybenh;
    public Boolean suyhohap;
    public Boolean soc;
    public Boolean phuphoicap;
    public Boolean roiloanhohap;
    public Boolean machnhanh;
    public Boolean tanghuyetap;
    public Boolean gongchi;
    public Boolean vamohoi;
    public Boolean thatdieu;
    public Boolean runggiatnhancau;
    public Boolean yeuchi;
    public Boolean lietthankinhso;
    public Boolean giatminhluckham;
    public Boolean benhsugiatminh;
    public String bieuhienkhac;
}
