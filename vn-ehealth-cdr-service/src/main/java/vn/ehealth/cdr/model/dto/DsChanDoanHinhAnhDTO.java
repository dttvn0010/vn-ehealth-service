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
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DULIEU;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DVKT;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_YLENH;

public class DsChanDoanHinhAnhDTO {

    public static class ChanDoanHinhAnhDTO {

        public String idhis;
        
        public DanhMuc dmKhoaDieuTri;
        public DanhMuc dmLoaiChanDoanHinhAnh;
        public DanhMuc dmChanDoanHinhAnh;
        
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
                        
        public List<FileDinhKem> dsFileDinhKemCdha = new ArrayList<>();
        
        public DichVuKyThuat generateDichVuKyThuat() {
            var dvkt = new DichVuKyThuat();
            dvkt.idhis = idhis;
            dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
         
            dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                    MessageUtils.get("text.CT"));
            
            dvkt.dmDVKT = dmChanDoanHinhAnh;
           
            dvkt.ngayThucHien = ngayThucHien;
            dvkt.bacSiThucHien = bacSiChuyenKhoa;
            
            dvkt.ketQua = ketQua;
            dvkt.ketLuan = ketLuan;
            dvkt.loiDan = loiDan;
            
            dvkt.dsFileDinhKem = dsFileDinhKemCdha;
            dvkt.trangThai = TRANGTHAI_DVKT.DA_XONG;
            
            return dvkt;
        }
        
        public Ylenh generateYlenh() {
            var ylenh = new Ylenh();
            ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_DVKT, MessageUtils.get("ylenh.dvkt"));
            ylenh.hienThi = String.format(MessageUtils.get("ylenh.dvkt.hienthi.template"), 1);
            ylenh.idhis = idhis;
            ylenh.ngayRaYlenh = ngayYeuCau;
            ylenh.bacSiRaYlenh = bacSiYeuCau;
            ylenh.ghiChu = noiDungYeuCau;
            ylenh.trangThai = TRANGTHAI_YLENH.DA_XONG;
            return ylenh;
        }
    }
    
    public String maTraoDoiHoSo;
    public List<ChanDoanHinhAnhDTO> dsChanDoanHinhAnh = new ArrayList<>();

}
