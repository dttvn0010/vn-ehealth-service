package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.CDRConstants.ThoiDiemDungThuoc;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class YlenhDTO {
    public DanhMuc dmLoaiYlenh;
    public DanhMuc dmMaBenhChanDoan;
    public String dienBien;
    public String dienBienQuanTrong;
    public String loiDan;
    public String ghiChu;
    
    public String chiDinhAnUong;
    public DanhMuc dmChiDinhCheDoAnUong;
    public DanhMuc dmChiDinhCheDoChamSoc;
    public DanhMuc dmChiDinhCapHoLy;    
    
    public static class ChiDinhThuocDTO {
        public DanhMuc dmThuoc;
        public DanhMuc dmDuongDungThuoc;
        public Integer lieuLuongSang;
        public Integer lieuLuongTrua;
        public Integer lieuLuongChieu;
        public Integer lieuLuongToi;
        public String donVi;
        public String ghiChu;        
        
        @JsonFormat(pattern="yyyy-MM-dd")
        public Date ngayBatDau;
        
        @JsonFormat(pattern="yyyy-MM-dd")
        public Date ngayKetThuc;
        
        public DonThuocChiTiet toDonThuocChiTiet() {
            var dtct = new DonThuocChiTiet();
            dtct.dmThuoc = this.dmThuoc;
            dtct.dmDuongDungThuoc = this.dmDuongDungThuoc;
            dtct.chiDanDungThuoc = this.ghiChu;
            dtct.ngayBatDau = this.ngayBatDau;
            dtct.ngayKetThuc = this.ngayKetThuc;
            
            dtct.dsTanSuatDungThuoc = new ArrayList<>();
            
            if(this.lieuLuongSang != null && this.lieuLuongSang > 0) {
                var tanSuatSang = new DonThuocChiTiet.TanSuatDungThuoc();
                tanSuatSang.soLuong = this.lieuLuongSang;
                tanSuatSang.donVi = this.donVi;
                tanSuatSang.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.SANG, MessageUtils.get("thoidiem.sang"));
                dtct.dsTanSuatDungThuoc.add(tanSuatSang);
            }
            
            if(this.lieuLuongTrua != null && this.lieuLuongTrua > 0) {
                var tanSuatTrua = new DonThuocChiTiet.TanSuatDungThuoc();
                tanSuatTrua.soLuong = this.lieuLuongTrua;
                tanSuatTrua.donVi = this.donVi;
                tanSuatTrua.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TRUA, MessageUtils.get("thoidiem.trua"));
                dtct.dsTanSuatDungThuoc.add(tanSuatTrua);
            }
            
            if(this.lieuLuongChieu != null && this.lieuLuongChieu > 0) {
                var tanSuatChieu = new DonThuocChiTiet.TanSuatDungThuoc();
                tanSuatChieu.soLuong = this.lieuLuongChieu;
                tanSuatChieu.donVi = this.donVi;
                tanSuatChieu.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.CHIEU, MessageUtils.get("thoidiem.chieu"));
                dtct.dsTanSuatDungThuoc.add(tanSuatChieu);
            }
            
            if(this.lieuLuongToi != null && this.lieuLuongToi > 0) {
                var tanSuatToi = new DonThuocChiTiet.TanSuatDungThuoc();
                tanSuatToi.soLuong = this.lieuLuongToi;
                tanSuatToi.donVi = this.donVi;
                tanSuatToi.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TOI, MessageUtils.get("thoidiem.toi"));
                dtct.dsTanSuatDungThuoc.add(tanSuatToi);
            }
            return dtct;
        }
    }
    
    public static class ChiDinhDVKT {
        public DanhMuc dmLoaiDVKT;
        public DanhMuc dmDVKT;
        public DanhMuc dmNoiThucHien;
        public String ghiChu;
        
        public DichVuKyThuat toDVKT() {
            var dvkt = new DichVuKyThuat();
            dvkt.dmLoaiDVKT = this.dmLoaiDVKT;
            dvkt.dmNoiThucHien = dmNoiThucHien;
            dvkt.dmDVKT = this.dmDVKT;
            dvkt.ghiChu = this.ghiChu;
            return dvkt;
        }
    }
    
    public List<ChiDinhThuocDTO> dsChiDinhThuoc = new ArrayList<>();
    public List<ChiDinhDVKT> dsChiDinhDVKT = new ArrayList<>();
    
    public DonThuoc generateDonThuoc() {
        var donThuoc = new DonThuoc();
        donThuoc.dsDonThuocChiTiet = FPUtil.transform(dsChiDinhThuoc, ChiDinhThuocDTO::toDonThuocChiTiet);        
        return donThuoc;
    }
    
    public List<DichVuKyThuat> generateDsDichVuKyThuat() {
        return FPUtil.transform(dsChiDinhDVKT, ChiDinhDVKT::toDVKT);
    }
    
    public Ylenh generateYlenh() {
        var ylenh = new Ylenh();
        
        ylenh.dmLoaiYlenh = dmLoaiYlenh;
        
        if(dmLoaiYlenh != null) {
            if(LoaiYlenh.YLENH_DIEU_TRI.equals(dmLoaiYlenh.ma)) {
                ylenh.hienThi = dienBien;
            }else if(LoaiYlenh.YLENH_THUOC.equals(dmLoaiYlenh.ma)) {
                int count = dsChiDinhThuoc != null? dsChiDinhThuoc.size() : 0;
                ylenh.hienThi = String.format(MessageUtils.get("ylenh.thuoc.hienthi.template"), count); 
            }else if(LoaiYlenh.YLENH_DVKT.equals(dmLoaiYlenh.ma)) {
                int count = dsChiDinhDVKT != null? dsChiDinhDVKT.size() : 0;                
                ylenh.hienThi = String.format(MessageUtils.get("ylenh.dvkt.hienthi.template"), count);
            }
        }
        
        ylenh.dmMaBenhChanDoan = dmMaBenhChanDoan;
        ylenh.dienBien = dienBien;
        ylenh.dienBienQuanTrong = dienBienQuanTrong;
        ylenh.loiDan = loiDan;
        ylenh.ghiChu = ghiChu;
        ylenh.chiDinhDieuTri = new Ylenh.ChiDinhDieuTri();
        
        ylenh.chiDinhDieuTri.anUong = chiDinhAnUong;
        ylenh.chiDinhDieuTri.dmCheDoAnUong = dmChiDinhCheDoAnUong;
        ylenh.chiDinhDieuTri.dmCheDoChamSoc = dmChiDinhCheDoChamSoc;
        ylenh.chiDinhDieuTri.dmCapHoLy = dmChiDinhCapHoLy;
        
        ylenh.ghiChu = ghiChu;
        return ylenh;
    }
}
