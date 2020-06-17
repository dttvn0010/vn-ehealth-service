package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;

public class GiaiPhauBenh {

    public String idhis;
    
    public DanhMuc dmKhoaDieuTri;
    
    public DanhMuc dmGiaiPhauBenh;
    public DanhMuc dmViTriLayMau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYteDTO bacSiYeuCau;
    public String noiDungYeuCau;
    
    public CanboYteDTO bacSiChuyenKhoa;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public String nhanXetDaiThe;
    public String nhanXetViThe;
    
    public DanhMuc dmKetQuaGiaiPhauBenh;
    public String motaChanDoanGiaiPhau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayLayMauSinhThiet;
        
    public List<FileDinhKem> dsFileDinhKemGpb = new ArrayList<>();
    
    public DichVuKyThuat toDichVuKyThuat() {
        var dvkt = new DichVuKyThuat();
        dvkt.idhis = idhis;
        dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
        
        dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.GIAI_PHAU_BENH, 
                MessageUtils.get("text.XRC"));
        
        dvkt.dmDVKT = dmGiaiPhauBenh;
        
        dvkt.ngayYeuCau = ngayYeuCau;
        dvkt.bacSiYeuCau = bacSiYeuCau;
        dvkt.noiDungYeuCau = noiDungYeuCau;
        dvkt.ngayThucHien = ngayThucHien;
        dvkt.bacSiThucHien = bacSiChuyenKhoa;
        
        dvkt.extra.put("dmViTriLayMau", dmViTriLayMau);
        dvkt.extra.put("nhanXetDaiThe", nhanXetDaiThe);
        dvkt.extra.put("nhanXetViThe", nhanXetViThe);
        dvkt.extra.put("dmKetQuaGiaiPhauBenh", dmKetQuaGiaiPhauBenh);
        dvkt.extra.put("motaChanDoanGiaiPhau", motaChanDoanGiaiPhau);
        dvkt.extra.put("ngayLayMauSinhThiet", ngayLayMauSinhThiet);
        
        dvkt.dsFileDinhKem = dsFileDinhKemGpb;
        
        return dvkt;
    }
    
}
