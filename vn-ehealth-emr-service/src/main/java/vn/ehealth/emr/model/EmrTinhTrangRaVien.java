package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrTinhTrangRaVien {

    public EmrDmContent emrDmNguyennhantuvong;
    
    public EmrDmContent emrDmKetQuaDieuTri;
    
    public EmrDmContent emrDmYhctKetQuaDieuTri;
    
    public EmrDmContent emrDmGiaiphaututhi;
        
    public EmrDmContent emrDmKetQuaGiaiPhauBenh;
  
    public EmrDmContent emrDmThoiDiemTuVong;
   
    public EmrDmContent emrDmLyDoTuVong;
    
    public Date ngaygiotuvong;

    public Boolean khamnghiemtuthi;

    public String motanguyennhantuvong;

    public String motagiaiphaututhi;

}
