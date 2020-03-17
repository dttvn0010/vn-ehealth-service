package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrQuanLyNguoiBenh {

    public EmrDmContent emrDmNoiGioiThieu;

    public EmrDmContent emrDmLoaiDoiTuongTaiChinh;

    public EmrDmContent emrDmLoaiRaVien;

    public EmrDmContent emrDmNoiTrucTiepVao;

    public EmrDmContent emrDmLoaiChuyenVien;

    public EmrDmContent emrDmCoSoKhamBenh;

    public EmrDmContent emrDmLoaiVaoVien;
    
    public String sovaovien;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiovaovien;
    
    public Integer vaovienlanthu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygioravien;
    
    //public ObjectId bacsikhamId;
    
    public EmrCanboYte bacsikham;    

    public Integer tongsongaydieutri;

    //public String tenbacsichoravien;
    public EmrCanboYte bacsichoravien;

    public String noichuyenden;
    
}
