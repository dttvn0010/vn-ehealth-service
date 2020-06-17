package vn.ehealth.cdr.model.dto;

import vn.ehealth.cdr.model.DieuTri;
import vn.ehealth.cdr.model.Ylenh;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.utils.CDRConstants.LoaiYlenh;
import vn.ehealth.cdr.utils.MessageUtils;

public class YlenhDieuTriDTO {

    public DanhMuc dmMaBenhChanDoan;
    public String dienBien;
    public String dienBienQuanTrong;
    public String loiDan;
    public String ghiChu;
    
    public String chiDinhAnUong;
    public DanhMuc dmChiDinhCheDoAnUong;
    public DanhMuc dmChiDinhCheDoChamSoc;
    public DanhMuc dmChiDinhCapHoLy;    
    
    public DieuTri generateDieuTri() {
        var dieuTri = new DieuTri();        
        dieuTri.dmMaBenhChanDoan = dmMaBenhChanDoan;
        dieuTri.dienBien = dienBien;
        dieuTri.dienBienQuanTrong = dienBienQuanTrong;
        dieuTri.loiDan = loiDan;
        dieuTri.ghiChu = ghiChu;
        dieuTri.chiDinh = new DieuTri.ChiDinh();
        
        dieuTri.chiDinh.anUong = chiDinhAnUong;
        dieuTri.chiDinh.dmCheDoAnUong = dmChiDinhCheDoAnUong;
        dieuTri.chiDinh.dmCheDoChamSoc = dmChiDinhCheDoChamSoc;
        dieuTri.chiDinh.dmCapHoLy = dmChiDinhCapHoLy;
        
        return dieuTri;
    }
    
    public Ylenh generateYlenh() {
        var ylenh = new Ylenh();
        ylenh.dmLoaiYlenh = new DanhMuc(LoaiYlenh.YLENH_DIEU_TRI, MessageUtils.get("ylenh.dieutri"));
        ylenh.dmMaBenhChanDoan = dmMaBenhChanDoan;
        ylenh.ghiChu = ghiChu;
        return ylenh;
    }
   
}
