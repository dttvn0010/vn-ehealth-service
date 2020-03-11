package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.model.ck.EmrCkChanTayMieng;
import vn.ehealth.emr.model.ck.EmrCkChucNangSinhHoat;
import vn.ehealth.emr.model.ck.EmrCkCoXuongKhop;
import vn.ehealth.emr.model.ck.EmrCkHoHap;
import vn.ehealth.emr.model.ck.EmrCkHuongDieuTriHuyetHoc;
import vn.ehealth.emr.model.ck.EmrCkHuongDieuTriTcm;
import vn.ehealth.emr.model.ck.EmrCkKhamPhuKhoa;
import vn.ehealth.emr.model.ck.EmrCkKhamSanKhoa;
import vn.ehealth.emr.model.ck.EmrCkKhamSoSinh;
import vn.ehealth.emr.model.ck.EmrCkMat;
import vn.ehealth.emr.model.ck.EmrCkMoiSinh;
import vn.ehealth.emr.model.ck.EmrCkPhuongPhapDieuTriUngBuou;
import vn.ehealth.emr.model.ck.EmrCkPhuongPhapHoiSinh;
import vn.ehealth.emr.model.ck.EmrCkQuaTrinhBenhLyTcm;
import vn.ehealth.emr.model.ck.EmrCkQuaTrinhSinhTruong;
import vn.ehealth.emr.model.ck.EmrCkSkTinhTrangSanPhu;
import vn.ehealth.emr.model.ck.EmrCkTamThan;
import vn.ehealth.emr.model.ck.EmrCkThanKinh;
import vn.ehealth.emr.model.ck.EmrCkTiemChung;
import vn.ehealth.emr.model.ck.EmrCkTienSuBanThanSanKhoa;
import vn.ehealth.emr.model.ck.EmrCkTienSuGiaDinh;
import vn.ehealth.emr.model.ck.EmrCkTienSuPhuKhoa;
import vn.ehealth.emr.model.ck.EmrCkTienSuSanKhoa;
import vn.ehealth.emr.model.ck.EmrCkTieuHoa;
import vn.ehealth.emr.model.ck.EmrCkTinhTrangRaVienMat;
import vn.ehealth.emr.model.ck.EmrCkTinhTrangSanPhu;
import vn.ehealth.emr.model.ck.EmrCkTinhTrangSoSinh;
import vn.ehealth.emr.model.ck.EmrCkToanThan;
import vn.ehealth.emr.model.ck.EmrCkTomTatBenhAnTcm;
import vn.ehealth.emr.model.ck.EmrCkTuanHoan;

@JsonInclude(Include.NON_NULL)
public class EmrBenhAn {
    
    public EmrDmContent emrDmMaBenhChandoanphanbiet;
    
    public EmrDmContent emrDmMaBenhChandoanbenhchinh;
    
    public EmrDmContent emrDmMaBenhChandoankemtheo;
    public List<EmrDmContent> emrDmMaBenhChandoankemtheos = new ArrayList<>();
    
    public String lydovaovien;
    public Integer vaongaythu;              //??
    public String quatrinhbenhly;
    public String tiensubanthan;
    public String tiemchung;
    public String diung;
    public String matuy;
    public String ruoubia;
    public String thuocla;
    public String thuoclao;
    public String dacdiemkhac;
    public String tiensugiadinh;
    public String quatrinhsinhtruong;
    public String tinhtrangsanphu;
    public String tinhtrangsosinh;
    public String phuongphaphoisinh;
    public String dichte;
    public String chucnangsinhhoat;
    public String toanthan;
    public Double mach;
    public Double nhietdo;
    public Integer huyetapcao;
    public Integer huyetapthap;
    public Integer nhiptho;
    public Double cannang;
    public Integer chieucao;
    public Integer vongnguc;
    public Integer vongdau;
    public String tuanhoan;
    public String hohap;
    public String tieuhoa;
    public String tietnieusinhduc;
    public String thankinh;
    public String coxuongkhop;
    public String taimuihong;
    public String ranghammat;
    public String noitietdinhduong;
    public String dalieu;
    public String mat;
    public String coquankhac;
    public String xetnghiemcanlamsang;
    public String tomtat;
    public String motachandoanbenhchinh;
    public String motachandoankemtheo;
    public String motachandoanphanbiet;    
    public String tamthan;
    public String tiensuphukhoa;
    public String tiensusankhoa;
    public String khamsankhoa;    
    public String khamphukhoa;
    public String khamsosinh;
    public String huyethoc;    
    public String taychanmieng;    
    public String sinhduc;    
    public String nhommau;    
    public String tienluong;
    public String huongdieutri;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaykybenhan;
    public String bacsylambenhan;
    
    //public String moisinh;
    
    public String nguonlay;     
    
    public EmrCkTinhTrangSanPhu emrCkTinhTrangSanPhu;    
    
    public EmrCkTinhTrangSoSinh emrCkTinhTrangSoSinh;    
    
    public EmrCkPhuongPhapHoiSinh emrCkPhuongPhapHoiSinh;    
    
    public EmrCkChucNangSinhHoat emrCkChucNangSinhHoat;    
    
    public EmrCkTuanHoan emrCkTuanHoan;    
    
    public EmrCkHoHap emrCkHoHap;      
    
    public EmrCkThanKinh emrCkThanKinh;
    
    public EmrCkCoXuongKhop emrCkCoXuongKhop;
    
    public EmrCkTamThan emrCkTamThan;    
    
    public EmrCkMat emrCkMat;      
    
    public EmrCkQuaTrinhSinhTruong emrCkQuaTrinhSinhTruong;    
    
    public EmrCkTiemChung emrCkTiemChung;    
    
    public EmrCkTienSuPhuKhoa emrCkTienSuPhuKhoa;    
    
    public EmrCkTienSuSanKhoa emrCkTienSuSanKhoa;    
    
    public EmrCkKhamSanKhoa emrCkKhamSanKhoa;    
    
    public EmrCkTienSuBanThanSanKhoa emrCkTienSuBanThanSanKhoa;    
    
    public EmrCkKhamPhuKhoa emrCkKhamPhuKhoa;    
    
    public EmrCkTienSuGiaDinh emrCkTienSuGiaDinh;    
    
    public EmrCkKhamSoSinh emrCkKhamSoSinh;    
    
    public EmrCkSkTinhTrangSanPhu emrCkSkTinhTrangSanPhu;
    
    // Add moi 02/04/2015
    
    public EmrCkMoiSinh emrCkMoiSinh;  
    
    // Add moi 10/04/2015
    
    public EmrCkToanThan emrCkToanThan;    
    
    public EmrCkTieuHoa emrCkTieuHoa;    
    
    public EmrCkHuongDieuTriHuyetHoc emrCkHuongDieuTriHuyetHoc;
    
    // Add moi 15/04/2015
    
    public EmrCkQuaTrinhBenhLyTcm emrCkQuaTrinhBenhLyTcm;    
    
    public EmrCkChanTayMieng emrCkChanTayMieng;    
    
    public EmrCkTomTatBenhAnTcm emrCkTomTatBenhAnTcm;
    
    // Add moi 16/04/2015
    
    public EmrCkHuongDieuTriTcm emrCkHuongDieuTriTcm;    
    
    public EmrCkPhuongPhapDieuTriUngBuou emrCkPhuongPhapDieuTriUngBuou;    
    
    public EmrCkTinhTrangRaVienMat emrCkTinhTrangRaVienMat;
    
    public String tyLeBaoHiem; //??
    
    public Boolean dungTuyen;  //??
    
    public Integer tyletonthuongloai1;  //??
    
    public Integer tyletonthuongloai2;  //??
    
    public Integer tyletonthuongloai3;  //??
    
    public Integer tyletonthuongloai4;  //??
    
    public Integer tyletonthuongloai5;  //??
    
    public EmrDmContent getEmrDmMaBenhChandoankemtheo() {
        if(emrDmMaBenhChandoankemtheos != null && emrDmMaBenhChandoankemtheos.size() > 0) {
            return emrDmMaBenhChandoankemtheos.get(0);
        }
        
        return null;
    }
        
}
