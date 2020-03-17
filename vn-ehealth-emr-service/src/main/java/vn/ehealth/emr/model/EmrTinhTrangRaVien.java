package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrTinhTrangRaVien {

    public EmrDmContent emrDmMaBenhNguyennhantuvong;
    
    public EmrDmContent emrDmKetQuaDieuTri;
    
    public EmrDmContent emrDmYhctKetQuaDieuTri;
    
    public EmrDmContent emrDmMaBenhGiaiphaututhi;
        
    public EmrDmContent emrDmKetQuaGiaiPhauBenh;
  
    public EmrDmContent emrDmThoiDiemTuVong;
   
    public EmrDmContent emrDmLyDoTuVong;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiotuvong;

    public Boolean khamnghiemtuthi;

    public String motanguyennhantuvong;

    public String motagiaiphaututhi;

}
