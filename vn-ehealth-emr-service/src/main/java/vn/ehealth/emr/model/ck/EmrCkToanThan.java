package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkToanThan {

    public String tinhthan;
    public String hinhdang;
    public String thetrang;
    public String da;
    public String niemmac;
    public Boolean biphu;
    public Boolean phutoanthan;
    public String trieuchungphu;
    public String trieuchungxuathuyet;
    public String longtocmong;
    public String tuyengiap;
    public String ganKichthuoc;
    public String ganMatdo;
    public String ganBogan;
    public String ganDau;
    public String ganThongtinbosung;
    public String lachKichthuoc;
    public String lachMatdo;
    public String lachBolach;
    public String lachMatlach;
    public String lachDaulach;
    public String lachThongtinbosung;
    public String hachKichthuoc;
    public String hachSoluong;
    public String hachDodidong;
    public String hachDodauhach;
    public String hachThongtinbosung;
    public String thanMota;
    public String timMota;
    public String phoiMota;
    public String vuMota;
    
    public String thieumau;
    
    public String khuyetTatDacBiet;
    
    public String matGan;
    public String hachViTri;
    public Integer tyletonthuongloai1;
    public Integer tyletonthuongloai2;
    public Integer tyletonthuongloai3;
    public Integer tyletonthuongloai4;
    public Integer tyletonthuongloai5;
}
