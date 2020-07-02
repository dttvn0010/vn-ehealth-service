package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.model.UongThuoc.VatTuYte;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class ChamSocDTO {
    
    public static class VatTuYteDTO {
        public DanhMuc dmVatTuYte;
        public int soLuong;
        
        public VatTuYte toModel() {
            var obj = new VatTuYte();
            obj.dmVatTuYte = dmVatTuYte;
            obj.soLuong = soLuong;
            return obj;
        }
    }
    
    public static class UongThuocDTO {
        public DanhMuc dmThuoc;
        public DanhMuc dmDuongDungThuoc;
        public DanhMuc dmThoiDiemDungThuoc;
                
        public int soLuong;    
        public String donVi;
        public CanboYteDTO bacSiChiDinh;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayChiDinh;
        
        public List<VatTuYteDTO> dsVatTuYte = new ArrayList<>();
        
        public UongThuoc toModel() {
            var obj = new UongThuoc();
            obj.dmThuoc = dmThuoc;
            obj.dmDuongDungThuoc = dmDuongDungThuoc;
            obj.dmThoiDiemDungThuoc = dmThoiDiemDungThuoc;
            obj.soLuong = soLuong;
            obj.donVi = donVi;
            obj.bacSiChiDinh = bacSiChiDinh;
            obj.ngayChiDinh = ngayChiDinh;
            obj.dsVatTuYte = FPUtil.transform(dsVatTuYte, VatTuYteDTO::toModel);
            return obj;
        }
    }
    
    public DanhMuc dmLoaiChamSoc;
    public DanhMuc dmTriGiac;
    public Double nhietDo;
    public Double mach;
    public Double huyetApCao;
    public Double huyetApThap;
    public Double nhipTho;
    public Double spO2;
    
    public String dau;
    public String daNiemMac;
    
    public String non;
    public String anUong;
    public String cheDoAn;
    public String cheDoUong;
    public String daiTien;
    public String tiemTruyen;
    public String truyenMau;
    public String danLuu;
    
    public String dienBienBenh;
    public String thucHienYlenh;
    public String ghiChu;
    public List<UongThuocDTO> dsUongThuoc = new ArrayList<>();
    
    public ChamSoc  generateChamSoc() {
        var chamSoc = new ChamSoc();
        
        chamSoc.dmLoaiChamSoc = dmLoaiChamSoc;
        chamSoc.dmTriGiac = dmTriGiac;
        chamSoc.nhietDo = nhietDo;
        chamSoc.mach = mach;
        chamSoc.huyetApCao = huyetApCao;
        chamSoc.huyetApThap = huyetApThap;
        chamSoc.nhipTho = nhipTho;
        chamSoc.spO2 = spO2;
        chamSoc.dau = dau;
        chamSoc.daNiemMac = daNiemMac;
        chamSoc.non = non;
        chamSoc.anUong = anUong;
        chamSoc.cheDoAn = cheDoAn;
        chamSoc.cheDoUong = cheDoUong;
        chamSoc.daiTien = daiTien;
        chamSoc.tiemTruyen = tiemTruyen;
        chamSoc.truyenMau = truyenMau;
        chamSoc.danLuu = danLuu;
        chamSoc.dienBienBenh = dienBienBenh;
        chamSoc.thucHienYlenh = thucHienYlenh;
        chamSoc.ghiChu = ghiChu;
        
        return chamSoc;
    }
    
    public List<UongThuoc> generateDsUongThuoc() {
        return FPUtil.transform(dsUongThuoc, UongThuocDTO::toModel);
    }
}
