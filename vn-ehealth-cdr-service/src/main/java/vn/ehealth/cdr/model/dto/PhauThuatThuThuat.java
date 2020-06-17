package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.DichVuKyThuat.ThanhVienThucHien;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;

public class PhauThuatThuThuat {

    public String idhis;
    
    public DanhMuc dmKhoaDieuTri;
    
    public DanhMuc dmPhauThuatThuThuat;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYteDTO bacSiYeuCau;
    public String noiDungYeuCau;
    
    public List<DanhMuc> dsDmMaBenhChanDoanSauPttt = new ArrayList<>();
    public String moTaChanDoanTruocPttt;
    
    public List<DanhMuc> dsDmMaBenhChanDoanTruocPttt = new ArrayList<>();    
    public String moTaChanDoanSauPttt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioPttt;
    
    public CanboYteDTO bacSiThucHien;
    public CanboYteDTO bacSiGayMe;
    
    public String chiDinhPttt;
    public String phuongPhapVoCam;
    public String luocDoPttt;
    public String trinhTuPttt;
    
    public static class ThanhVienPttt {
        public DanhMuc dmVaiTro;        
        public CanboYteDTO bacSiPttt;      
        
        public ThanhVienThucHien toDVKTThanhVien() {
            var tv = new ThanhVienThucHien();
            tv.dmVaiTro = dmVaiTro;
            tv.bacSi = bacSiPttt;
            return tv;
        }
    }
    
    public List<ThanhVienPttt> dsThanhVienPttt = new ArrayList<>();
    
    public List<FileDinhKem> dsFileDinhKemPttt = new ArrayList<>();
    
    public DichVuKyThuat toDichVuKyThuat() {
        
        var dvkt = new DichVuKyThuat();
        dvkt.idhis = idhis;
        dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
        
        dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                MessageUtils.get("text.SUR"));
        
        dvkt.dmDVKT = dmPhauThuatThuThuat;
        
        dvkt.ngayYeuCau = ngayYeuCau;
        dvkt.bacSiYeuCau = bacSiYeuCau;
        dvkt.noiDungYeuCau = noiDungYeuCau;
        
        dvkt.dsDmMaBenhChanDoanTruoc = dsDmMaBenhChanDoanTruocPttt;
        dvkt.moTaChanDoanTruoc = moTaChanDoanTruocPttt;
        dvkt.dsDmMaBenhChanDoanSau = dsDmMaBenhChanDoanSauPttt;
        dvkt.moTaChanDoanSau = moTaChanDoanSauPttt;
        
        dvkt.ngayThucHien = ngayGioPttt;
        dvkt.bacSiThucHien = bacSiThucHien;
        
        dvkt.dsThanhVien = DataConvertUtil.transform(dsThanhVienPttt, ThanhVienPttt::toDVKTThanhVien);
        
        dvkt.extra.put("bacSiGayMe", bacSiGayMe);
        dvkt.extra.put("chiDinhPttt", chiDinhPttt);
        dvkt.extra.put("phuongPhapVoCam", phuongPhapVoCam);
        dvkt.extra.put("luocDoPttt", luocDoPttt);
        dvkt.extra.put("trinhTuPttt", trinhTuPttt); 
        
        dvkt.dsFileDinhKem = dsFileDinhKemPttt;
        
        return dvkt;
        
    }
}