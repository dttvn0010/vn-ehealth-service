package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DONTHUOC;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class DsDonThuocDTO {

    public static class TanSuatDungThuocDTO {
        public Integer soLuong;
        public String donVi;
        public DanhMuc dmThoiDiemDungThuoc;
        
        public DonThuocChiTiet.TanSuatDungThuoc toTanSuatDungThuoc() {
            var obj = new DonThuocChiTiet.TanSuatDungThuoc();
            obj.soLuong = soLuong;
            obj.donVi = donVi;
            obj.dmThoiDiemDungThuoc = dmThoiDiemDungThuoc;
            return obj;
        }
    }
    
    public static class LieuLuongDungThuocDTO {
        public Integer soLuong;
        public String donVi;
                
        public LieuLuongDungThuocDTO() {
            
        }
        
        public LieuLuongDungThuocDTO(String lieuLuong) {
            if(lieuLuong == null) return;
            
            var items = List.of(lieuLuong.split(" "));
            items = FPUtil.filter(items, x -> !StringUtils.isBlank(x));
            
            try {
                this.soLuong = Integer.parseInt(items.get(0));
            }catch(NumberFormatException e) {
                
            }
            
            if(this.soLuong != null && items.size() > 0) {
                this.donVi = DataConvertUtil.joinString(items.subList(1, items.size()), " ");
            }                
        }
        
        public DonThuocChiTiet.LieuLuongDungThuoc toLieuLuongDungThuoc() {
            var obj = new DonThuocChiTiet.LieuLuongDungThuoc();
            obj.soLuong = soLuong;
            obj.donVi = donVi;
            return obj;
        }
    }
    
    public static class DonThuocChiTietDTO {        
        
        public DanhMuc dmThuoc;
        public DanhMuc dmDuongDungThuoc;
        public DanhMuc dmTanSuatDungThuoc;
         
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayBatDau;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayKetThuc;
        
        public LieuLuongDungThuocDTO lieuLuongThuoc;
        public List<TanSuatDungThuocDTO> dsTanSuatDungThuoc;
        
        public String chiDanDungThuoc;
        public String bietDuoc;
        
        public DonThuocChiTiet toDonThuocChiTiet() {
             
            var obj = new DonThuocChiTiet();
            obj.dmThuoc = dmThuoc;
            obj.dmDuongDungThuoc = dmDuongDungThuoc;
            obj.ngayBatDau = ngayBatDau;
            obj.ngayKetThuc = ngayKetThuc;
            obj.chiDanDungThuoc = chiDanDungThuoc;
            if(lieuLuongThuoc != null) {
                obj.lieuLuongThuoc = lieuLuongThuoc.toLieuLuongDungThuoc();
            }
            obj.dsTanSuatDungThuoc = FPUtil.transform(dsTanSuatDungThuoc, TanSuatDungThuocDTO::toTanSuatDungThuoc);
            obj.bietDuoc = bietDuoc;
            
            var cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            if(ngayKetThuc == null || ngayKetThuc.getTime() < cal.getTimeInMillis()) {
                obj.trangThai = TRANGTHAI_DONTHUOC.DA_XONG;
            }
          
            return obj;
        }
    }
    
    public static class DonThuocDTO {
        
        public String idhis;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayKeDon;
        public CanboYteDTO bacSiKeDon;
        public String soDon;
        
        public List<DonThuocChiTietDTO> dsDonThuocChiTiet = new ArrayList<>();
        public List<FileDinhKem> dsFileDinhKemDonThuoc = new ArrayList<>();      
        
        public DonThuoc generateDonThuoc() {
            var donThuoc = new DonThuoc();
            donThuoc.idhis = idhis;
            donThuoc.ngayKeDon = ngayKeDon;
            donThuoc.bacSiKeDon = bacSiKeDon;
            donThuoc.soDon = soDon;
            donThuoc.dsFileDinhKemDonThuoc = dsFileDinhKemDonThuoc;
            donThuoc.dsDonThuocChiTiet = FPUtil.transform(dsDonThuocChiTiet, DonThuocChiTietDTO::toDonThuocChiTiet);
            
            if(FPUtil.allMatch(donThuoc.dsDonThuocChiTiet, x -> x.trangThai == TRANGTHAI_DONTHUOC.DA_XONG)) {
                donThuoc.trangThai = TRANGTHAI_DONTHUOC.DA_XONG;
            }
            return donThuoc;
        }
        
        public Ylenh generateYlenh() {
            var ylenh = new Ylenh();
            ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_THUOC, MessageUtils.get("ylenh.thuoc"));
            int count = dsDonThuocChiTiet != null? dsDonThuocChiTiet.size() : 0;
            ylenh.hienThi = String.format(MessageUtils.get("ylenh.thuoc.hienthi.template"), count);
            ylenh.idhis = idhis;
            ylenh.ngayRaYlenh = ngayKeDon;
            ylenh.bacSiRaYlenh = bacSiKeDon;
            return ylenh;
        }
    }
    
    public String maTraoDoiHoSo;
    public List<DonThuocDTO> dsDonThuoc = new ArrayList<>();
}
