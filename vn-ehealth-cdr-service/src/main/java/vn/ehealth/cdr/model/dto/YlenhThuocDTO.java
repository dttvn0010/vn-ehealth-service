package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.CDRConstants.ThoiDiemDungThuoc;

public class YlenhThuocDTO {

    public static class DonThuocChiTietDTO {
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
    
    public List<DonThuocChiTietDTO> dsChiDinhThuoc = new ArrayList<>();
    
    public DonThuoc generateDonThuoc() {
        var donThuoc = new DonThuoc();
        donThuoc.dsDonThuocChiTiet = new ArrayList<>();
        
        if(dsChiDinhThuoc != null) {
            for(var chiDinhThuoc : dsChiDinhThuoc) {
                var dtct = new DonThuoc.DonThuocChiTiet();
                dtct.dmThuoc = chiDinhThuoc.dmThuoc;
                dtct.dmDuongDungThuoc = chiDinhThuoc.dmDuongDungThuoc;
                dtct.chiDanDungThuoc = chiDinhThuoc.ghiChu;
                dtct.ngayBatDau = chiDinhThuoc.ngayBatDau;
                dtct.ngayKetThuc = chiDinhThuoc.ngayKetThuc;
                
                dtct.dsLieuLuongThuoc = new ArrayList<>();
                
                if(chiDinhThuoc.lieuLuongSang != null && chiDinhThuoc.lieuLuongSang > 0) {
                    var lieuLuongSang = new DonThuoc.LieuLuongDungThuoc();
                    lieuLuongSang.soLuong = chiDinhThuoc.lieuLuongSang;
                    lieuLuongSang.donVi = chiDinhThuoc.donVi;
                    lieuLuongSang.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.SANG, MessageUtils.get("thoidiem.sang"));
                    dtct.dsLieuLuongThuoc.add(lieuLuongSang);
                }
                
                if(chiDinhThuoc.lieuLuongTrua != null && chiDinhThuoc.lieuLuongTrua > 0) {
                    var lieuLuongTrua = new DonThuoc.LieuLuongDungThuoc();
                    lieuLuongTrua.soLuong = chiDinhThuoc.lieuLuongTrua;
                    lieuLuongTrua.donVi = chiDinhThuoc.donVi;
                    lieuLuongTrua.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TRUA, MessageUtils.get("thoidiem.trua"));
                    dtct.dsLieuLuongThuoc.add(lieuLuongTrua);
                }
                
                if(chiDinhThuoc.lieuLuongChieu != null && chiDinhThuoc.lieuLuongChieu > 0) {
                    var lieuLuongChieu = new DonThuoc.LieuLuongDungThuoc();
                    lieuLuongChieu.soLuong = chiDinhThuoc.lieuLuongChieu;
                    lieuLuongChieu.donVi = chiDinhThuoc.donVi;
                    lieuLuongChieu.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.CHIEU, MessageUtils.get("thoidiem.chieu"));
                    dtct.dsLieuLuongThuoc.add(lieuLuongChieu);
                }
                
                if(chiDinhThuoc.lieuLuongToi != null && chiDinhThuoc.lieuLuongToi > 0) {
                    var lieuLuongToi = new DonThuoc.LieuLuongDungThuoc();
                    lieuLuongToi.soLuong = chiDinhThuoc.lieuLuongToi;
                    lieuLuongToi.donVi = chiDinhThuoc.donVi;
                    lieuLuongToi.dmThoiDiemDungThuoc = new DanhMuc(ThoiDiemDungThuoc.TOI, MessageUtils.get("thoidiem.toi"));
                    dtct.dsLieuLuongThuoc.add(lieuLuongToi);
                }
                donThuoc.dsDonThuocChiTiet.add(dtct);
            }            
        }
        return donThuoc;
    }
    
    public Ylenh generateYlenh() {
        var ylenh = new Ylenh();
        ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_THUOC, MessageUtils.get("ylenh.thuoc"));
        return ylenh;
    }
}
