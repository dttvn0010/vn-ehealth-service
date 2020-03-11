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

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaygiovaovien;
    
    public Integer vaovienlanthu;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaygioravien;    

    public String tenbacsikham;    

    public Integer tongsongaydieutri;

    public String tenbacsichoravien;

    public String noichuyenden;
    
}
