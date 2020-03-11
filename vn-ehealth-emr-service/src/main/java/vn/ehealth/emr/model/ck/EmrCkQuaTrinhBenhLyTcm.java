package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkQuaTrinhBenhLyTcm {

    public Boolean sot;
    public Boolean phatban;
    public Boolean loetmieng;
    public Boolean giatminh;
    public Integer giatminh24h;
    public Boolean nonoi;
    public Boolean cogiat;
    public Boolean runchi;
    public String dauhieukhac;
    public String dieutrituyentruoc;
    public String motakhac;
}
