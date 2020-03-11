package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrTongKetRaVien {

    public String dienbienlamsang;

    public String canlamsang;

    public String phuongphapdieutri;

    public String tinhtrangnguoibenh;

    public String chidandieutri;

    public String nguoigiaohoso;

    public Date ngaygiaohoso;

    public String nguoinhanhoso;

    public Date ngaynhanhoso;

    public String bacsydieutri;

    public Date ngaybacsydieutriky;

    public Integer soToXQuang;
    
    public Integer soToCTScanner;
    
    public Integer soToSieuAm;
    
    public Integer soToXetNghiem;
    
    public Integer soToKhac;
}
