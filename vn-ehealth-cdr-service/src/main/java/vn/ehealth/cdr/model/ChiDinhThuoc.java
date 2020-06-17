package vn.ehealth.cdr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ChiDinhThuoc {

    public DanhMuc dmThuoc;
    public int lieuLuongSang;
    public int lieuLuongTrua;
    public int lieuLuongChieu;
    public int lieuLuongToi;
    
    public DanhMuc dmDuongDungThuoc;
    public String ghiChu;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    public Date ngayBatDau;
    
    @JsonFormat(pattern="yyyy-MM-dd")
    public Date ngayKetThuc;
    
    
}
