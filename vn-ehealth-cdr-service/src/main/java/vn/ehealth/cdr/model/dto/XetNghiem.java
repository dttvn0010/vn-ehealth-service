package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.FileDinhKem;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.dto.XetNghiem.XetNghiemDichVu.XetNghiemKetQua;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;

public class XetNghiem {

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
            
    public static class XetNghiemDichVu {
        public String idhis;
      
        public DanhMuc dmXetNghiem;        
               
        public static class XetNghiemKetQua {
            
            public DanhMuc dmDichKetQuaXetNghiem;
            
            public DanhMuc dmChiSoXetNghiem;

            public String giaTriDo;           
            
            public DichVuKyThuat.KetQuaXetNghiem toDVKTKetQuaXetNghiem() {
                var kqxn = new DichVuKyThuat.KetQuaXetNghiem();
                kqxn.dmDichKetQuaXetNghiem = dmDichKetQuaXetNghiem;
                kqxn.dmChiSoXetNghiem = dmChiSoXetNghiem;
                kqxn.giaTriDo = giaTriDo;
                return kqxn;
            }
        }

        public List<XetNghiemKetQua> dsKetQuaXetNghiem = new ArrayList<>();
     
    }
    
    public List<XetNghiemDichVu> dsDichVuXetNghiem = new ArrayList<>();
    public List<FileDinhKem> dsFileDinhKemXetNghiem = new ArrayList<>();
    
    public List<DichVuKyThuat> toDsDichVuKyThuat() {
        var dsDVKT = new ArrayList<DichVuKyThuat>();
        
        if(dsDichVuXetNghiem != null) {
            for(var dvxn : dsDichVuXetNghiem) {
                var dvkt = new DichVuKyThuat();
                dvkt.idhis = dvxn.idhis;
                dvkt.dmKhoaDieuTri = dmKhoaDieuTri;
                
                dvkt.bacSiYeuCau = bacSiYeuCau;
                dvkt.ngayYeuCau = ngayYeuCau;
                dvkt.noiDungYeuCau = noiDungYeuCau;
                
                dvkt.ngayThucHien = ngayThucHien;
                dvkt.bacSiThucHien = bacSiXetNghiem;
                dvkt.nhanXet = nhanXet;
                
                dvkt.dmLoaiDVKT = new DanhMuc(LoaiDichVuKT.XET_NGHIEM, 
                    MessageUtils.get("text.LAB"));
                
                dvkt.dmDVKT = dvxn.dmXetNghiem;
                
                dvkt.nhanXet = nhanXet;
                
                dvkt.dsKetQuaXetNghiem = DataConvertUtil.transform(dvxn.dsKetQuaXetNghiem, XetNghiemKetQua::toDVKTKetQuaXetNghiem);
                
                dvkt.extra.put("dmLoaiXetNghiem", dmLoaiXetNghiem);
                
                dsDVKT.add(dvkt);
            }
        }
        
        return dsDVKT;
    }
}
