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
    }
    
    public static class ChiDinhDVKT {
        public DanhMuc dmLoaiDVKT;
        public DanhMuc dmDVKT;
        public DanhMuc dmNoiThucHien;
        public String ghiChu;
    }
    
    public List<ChiDinhThuocDTO> dsChiDinhThuoc = new ArrayList<>();
    public List<ChiDinhDVKT> dsChiDinhDVKT = new ArrayList<>();
    
    public DonThuoc generateDonThuoc() {
        var donThuoc = new DonThuoc();
        donThuoc.dsDonThuocChiTiet = new ArrayList<>();
        
        if(dsChiDinhThuoc != null) {
            for(var chiDinhThuoc : dsChiDinhThuoc) {
                var dtct = new DonThuocChiTiet();
                dtct.dmThuoc = chiDinhThuoc.dmThuoc;
                dtct.dmDuongDungThuoc = chiDinhThuoc.dmDuongDungThuoc;
                dtct.chiDanDungThuoc = chiDinhThuoc.ghiChu;
                dtct.ngayBatDau = chiDinhThuoc.ngayBatDau;
                dtct.ngayKetThuc = chiDinhThuoc.ngayKetThuc;
                
                dtct.dsTanSuatDungThuoc = new ArrayList<>();
                
                if(chiDinhThuoc.lieuLuongSang != null && chiDinhThuoc.lieuLuongSang > 0) {
                    var tanSuatSang = new DonThuocChiTiet.TanSuatDungThuoc();
                    tanSuatSang.soLuong = chiDinhThuoc.lieuLuongSang;
                    tanSuatSang.donVi = chiDinhThuoc.donVi;
                    tanSuatSang.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.SANG, MessageUtils.get("thoidiem.sang"));
                    dtct.dsTanSuatDungThuoc.add(tanSuatSang);
                }
                
                if(chiDinhThuoc.lieuLuongTrua != null && chiDinhThuoc.lieuLuongTrua > 0) {
                    var tanSuatTrua = new DonThuocChiTiet.TanSuatDungThuoc();
                    tanSuatTrua.soLuong = chiDinhThuoc.lieuLuongTrua;
                    tanSuatTrua.donVi = chiDinhThuoc.donVi;
                    tanSuatTrua.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TRUA, MessageUtils.get("thoidiem.trua"));
                    dtct.dsTanSuatDungThuoc.add(tanSuatTrua);
                }
                
                if(chiDinhThuoc.lieuLuongChieu != null && chiDinhThuoc.lieuLuongChieu > 0) {
                    var tanSuatChieu = new DonThuocChiTiet.TanSuatDungThuoc();
                    tanSuatChieu.soLuong = chiDinhThuoc.lieuLuongChieu;
                    tanSuatChieu.donVi = chiDinhThuoc.donVi;
                    tanSuatChieu.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.CHIEU, MessageUtils.get("thoidiem.chieu"));
                    dtct.dsTanSuatDungThuoc.add(tanSuatChieu);
                }
                
                if(chiDinhThuoc.lieuLuongToi != null && chiDinhThuoc.lieuLuongToi > 0) {
                    var tanSuatToi = new DonThuocChiTiet.TanSuatDungThuoc();
                    tanSuatToi.soLuong = chiDinhThuoc.lieuLuongToi;
                    tanSuatToi.donVi = chiDinhThuoc.donVi;
                    tanSuatToi.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TOI, MessageUtils.get("thoidiem.toi"));
                    dtct.dsTanSuatDungThuoc.add(tanSuatToi);
                }
                donThuoc.dsDonThuocChiTiet.add(dtct);
            }            
        }
        return donThuoc;
    }
    
    public List<DichVuKyThuat> generateDsDichVuKyThuat() {
        var dsDVKT = new ArrayList<DichVuKyThuat>();
        
        for(var chiDinhDVKT : dsChiDinhDVKT) {
           var dvkt = new DichVuKyThuat();
           dvkt.dmLoaiDVKT = chiDinhDVKT.dmLoaiDVKT;
           dvkt.dmDVKT = chiDinhDVKT.dmDVKT;
           dvkt.ghiChu = ghiChu;
           dsDVKT.add(dvkt);
        }
        
        return dsDVKT;
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
