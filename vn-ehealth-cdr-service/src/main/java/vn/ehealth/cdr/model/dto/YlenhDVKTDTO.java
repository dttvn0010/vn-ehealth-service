package vn.ehealth.cdr.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import vn.ehealth.cdr.model.DichVuKyThuat;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;

public class YlenhDVKTDTO {

    public DanhMuc dmMaBenhChanDoan;
    public String ghiChu;
    public DanhMuc dmLoaiDVKT;
    public DanhMuc dmNhomDVKT;
    public List<DanhMuc> dsDmDVKT = new ArrayList<>();
    
    public List<DichVuKyThuat> generateDsDichVuKyThuat() {
        var dsDVKT = new ArrayList<DichVuKyThuat>();
        
        for(var dmDVKT : dsDmDVKT) {
           var dvkt = new DichVuKyThuat();
           dvkt.dmLoaiDVKT = dmLoaiDVKT;
           dvkt.dmNhomDVKT = dmNhomDVKT;
           dvkt.dmDVKT = dmDVKT;
           dsDVKT.add(dvkt);
        }
        
        return dsDVKT;
    }
    
    public Ylenh generateYlenh() {
        var ylenh = new Ylenh();
        ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_DVKT, MessageUtils.get("ylenh.dvkt"));
        String dvkt = "";
        
        if(dmNhomDVKT != null && !StringUtils.isEmpty(dmNhomDVKT.ten)) {
            dvkt = dmNhomDVKT.ten;
        }else if(dsDmDVKT.size() > 0){
            dvkt = dsDmDVKT.get(0).ten;
        }
        
        ylenh.hienThi = String.format(MessageUtils.get("ylenh.dvkt.hienthi.template"), dvkt);
        ylenh.dmMaBenhChanDoan = dmMaBenhChanDoan;
        ylenh.dmLoaiDVKT = dmLoaiDVKT;
        ylenh.dmNhomDVKT = dmNhomDVKT;
        ylenh.ghiChu = ghiChu;
        return ylenh;
    }
}
