package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.CDRConstants.TRANGTHAI_DVKT;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class DSXetNghiemDTO {

    public static class DanhMucChiSoXetNghiem {
        public String ma;
        public String ten;
        public String donvi;
        public String chiSoBinhThuong;
    }
    
    public static class XetNghiemKetQuaDTO {
        
        public DanhMuc dmDichKetQuaXetNghiem;
        
        public DanhMucChiSoXetNghiem dmChiSoXetNghiem;

        public String giaTriDo;           
        
        public DichVuKyThuat.KetQuaXetNghiem toDVKTKetQuaXetNghiem() {
            var kqxn = new DichVuKyThuat.KetQuaXetNghiem();
            kqxn.dmDichKetQuaXetNghiem = dmDichKetQuaXetNghiem;
            if(dmChiSoXetNghiem != null) {
                kqxn.dmChiSoXetNghiem = new DanhMuc();
                kqxn.dmChiSoXetNghiem.ma = dmChiSoXetNghiem.ma;
                kqxn.dmChiSoXetNghiem.ten = dmChiSoXetNghiem.ten;
                kqxn.dmChiSoXetNghiem.extra = new HashMap<String, Object>();
                kqxn.dmChiSoXetNghiem.extra.put("donVi", dmChiSoXetNghiem.donvi);
                kqxn.dmChiSoXetNghiem.extra.put("chiSoBinhThuong", dmChiSoXetNghiem.chiSoBinhThuong);
            }
            kqxn.giaTriDo = giaTriDo;
            return kqxn;
        }
    }
    
    public static class XetNghiemDichVuDTO {
        public String idhis;
      
        public DanhMuc dmXetNghiem;        
               
        public List<XetNghiemKetQuaDTO> dsKetQuaXetNghiem = new ArrayList<>();
     
    }
    
    public static class XetNghiemDTO {

        public String idhis;
        
        public DanhMuc dmKhoaDieuTri;
        
        public DanhMuc dmLoaiXetNghiem;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayYeuCau;
        
        public CanboYteDTO bacSiYeuCau;
        public String noiDungYeuCau;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayThucHien;
        
        public CanboYteDTO bacSiXetNghiem;    
        public String nhanXet;
                 
        public List<XetNghiemDichVuDTO> dsDichVuXetNghiem = new ArrayList<>();
        public List<FileDinhKem> dsFileDinhKemXetNghiem = new ArrayList<>();
        
        public @Nonnull List<DichVuKyThuat> generateDsDichVuKyThuat() {
            var dsDVKT = new ArrayList<DichVuKyThuat>();
            
            if(dsDichVuXetNghiem != null) {
                for(var dvxn : dsDichVuXetNghiem) {
                    var dvkt = new DichVuKyThuat();
                    dvkt.idhis = dvxn.idhis;
                    dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
                    
                    dvkt.ngayThucHien = ngayThucHien;
                    dvkt.bacSiThucHien = bacSiXetNghiem;
                    dvkt.nhanXet = nhanXet;
                    
                    dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.XET_NGHIEM, 
                        MessageUtils.get("text.LAB"));
                    
                    dvkt.dmDVKT = dvxn.dmXetNghiem;
                    
                    dvkt.nhanXet = nhanXet;
                    
                    dvkt.dsKetQuaXetNghiem = DataConvertUtil.transform(dvxn.dsKetQuaXetNghiem, XetNghiemKetQuaDTO::toDVKTKetQuaXetNghiem);
                    
                    dvkt.extra.put("dmLoaiXetNghiem", dmLoaiXetNghiem);
                    dvkt.trangThai = TRANGTHAI_DVKT.DA_XONG;
                    
                    dsDVKT.add(dvkt);
                }
            }
            
            return dsDVKT;
        }
        
        public Ylenh generateYlenh() {
            var ylenh = new Ylenh();
            ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_DVKT, MessageUtils.get("ylenh.dvkt"));
            ylenh.hienThi = String.format(MessageUtils.get("ylenh.dvkt.hienthi.template"), 1);
            ylenh.idhis = idhis;
            ylenh.ngayRaYlenh = ngayYeuCau;
            ylenh.bacSiRaYlenh = bacSiYeuCau;
            ylenh.ghiChu = noiDungYeuCau;
            return ylenh;
        }
    }
    
    public String maTraoDoiHoSo;
    public List<XetNghiemDTO> dsXetNghiem = new ArrayList<>();
}
