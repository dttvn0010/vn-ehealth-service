package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;

public class DsDonThuocDTO {

    public static class TanSuatDungThuocDTO {
        public Integer soLuong;
        public String donVi;
        public DanhMuc dmThoiDiemDungThuoc;
        
        public DonThuoc.TanSuatDungThuoc toTanSuatDungThuoc() {
            var obj = new DonThuoc.TanSuatDungThuoc();
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
        
        public DonThuoc.LieuLuongDungThuoc toLieuLuongDungThuoc() {
            var obj = new DonThuoc.LieuLuongDungThuoc();
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
        
        public DonThuoc.DonThuocChiTiet toDonThuocChiTiet() {
             
            var obj = new DonThuoc.DonThuocChiTiet();
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
            return donThuoc;
        }
        
        public Ylenh generateYlenh() {
            var ylenh = new Ylenh();
            ylenh.idhis = idhis;
            ylenh.ngayRaYlenh = ngayKeDon;
            ylenh.bacSiRaYlenh = bacSiKeDon;
            return ylenh;
        }
      
    }
    
    public String maTraoDoiHoSo;
    public List<DonThuocDTO> dsDonThuoc = new ArrayList<>();
}
