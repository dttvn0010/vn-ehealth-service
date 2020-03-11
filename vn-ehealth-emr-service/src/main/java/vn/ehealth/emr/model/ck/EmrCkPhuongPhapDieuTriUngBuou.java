package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkPhuongPhapDieuTriUngBuou {

    public Integer kieudieutri;
    
    public Double tiaxatienphautaiu;

    public Double tiaxatienphautaihach;

    public Double tiaxadonthuantaiu;

    public Double tiaxadonthuantaihach;

    public String phauthuatu;

    public Double tiaxahauphautaiu;

    public Double tiaxahauphautaihach;

    public String hoachat;

    public Integer sodot;

    
    public Integer dapung;

    public String dieutrikhac; 
    
    public Integer idchuyenkhoa;
}
