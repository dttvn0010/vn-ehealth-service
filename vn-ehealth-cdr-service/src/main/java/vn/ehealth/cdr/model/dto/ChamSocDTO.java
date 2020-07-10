package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import vn.ehealth.cdr.model.ChamSoc;
import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.model.UongThuoc.VatTuYte;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
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
        public String donThuocChiTietId;
        public String maThoiDiemDungThuoc;
        
        public List<VatTuYteDTO> dsVatTuYte = new ArrayList<>();
        
        public UongThuoc toModel() {
            var obj = new UongThuoc();
            if(StringUtils.isEmpty(donThuocChiTietId)) {
                throw new RuntimeException("Missing field donThuocChiTietId for chamSoc.uongThuoc");
            }
            obj.donThuocChiTietRef = EmrRef.fromObjectId(DonThuocChiTiet.class.getName(), donThuocChiTietId);
            obj.maThoiDiemUongThuoc = maThoiDiemDungThuoc != null? maThoiDiemDungThuoc : "";
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