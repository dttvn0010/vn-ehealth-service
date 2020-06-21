package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;

public class DsThamDoChucNangDTO {

    public static class ThamDoChucNangDTO {

        public String idhis;
        
        public DanhMuc dmKhoaDieuTri;
        
        public DanhMuc dmThamDoChucNang;
        
        public DanhMuc dmLoaiThamDoChucNang;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayYeuCau;
        
        public CanboYteDTO bacSiYeuCau;
        public String noiDungYeuCau;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayThucHien;
        
        public CanboYteDTO bacSiChuyenKhoa;
        
        public String ketQua;
        public String ketLuan;
        public String loiDan;    
        
        public List<FileDinhKem> dsFileDinhKemTdcn = new ArrayList<>();
        
        public DichVuKyThuat generateDichVuKyThuat() {
            var dvkt = new DichVuKyThuat();
            dvkt.idhis = idhis;
            dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
            
            dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.THAM_DO_CHUC_NANG, 
                    MessageUtils.get("text.XRC"));
            
            dvkt.dmDVKT = dmThamDoChucNang;
            
            dvkt.ngayThucHien = ngayThucHien;
            dvkt.bacSiThucHien = bacSiChuyenKhoa;
            
            dvkt.ketQua = ketQua;
            dvkt.ketLuan = ketLuan;
            dvkt.loiDan = loiDan;
            
            dvkt.dsFileDinhKem = dsFileDinhKemTdcn;
            
            return dvkt;
        }
        
        public Ylenh generateYlenh() {
            var ylenh = new Ylenh();    
            ylenh.idhis = idhis;
            ylenh.ngayRaYlenh = ngayYeuCau;
            ylenh.bacSiRaYlenh = bacSiYeuCau;
            ylenh.ghiChu = noiDungYeuCau;
            return ylenh;
        }
    }

    public String maTraoDoiHoSo;
    public List<ThamDoChucNangDTO> dsThamDoChucNang = new ArrayList<>();
}