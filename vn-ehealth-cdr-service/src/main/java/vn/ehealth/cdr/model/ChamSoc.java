package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "cham_soc")
public class ChamSoc {
    @Id public ObjectId id;       
    public ObjectId hoSoBenhAnId;    
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    public int trangThai;
    
    public DanhMuc dmTriGiac;
    public double nhietDo;
    public double mach;
    public double huyetApCao;
    public double huyetApThap;
    public double nhipTho;
    public double spO2;
    
    public String non;
    public String anUong;
    public String cheDoAn;
    public String cheDoUong;
    public String daiTien;
    public String tiemTruyen;
    public String truyenMau;
    public String danLuu;
    
    public String dienBienYlenh;
    public String thucHienYlenh;
    public String ghiChu;
    
    public static class VatTuYte {
        public DanhMuc dmVatTuYte;
        public int soLuong;
    }
    
    public static class UongThuoc {
        public DanhMuc dmThuoc;
        public int lieuLuong;
        public int thoiDiemTrongNgay;
        public List<VatTuYte> dsVatTuYte = new ArrayList<>();
    }
    
    public List<UongThuoc> dsUongThuoc = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getHoSoBenhAnId() {
        return ObjectIdUtil.idToString(hoSoBenhAnId);
    }
    
    public void setHoSoBenhAnId(String hoSoBenhAnId) {
        this.hoSoBenhAnId = ObjectIdUtil.stringToId(hoSoBenhAnId);            
    }

    public String getBenhNhanId() {
        return ObjectIdUtil.idToString(benhNhanId);
    }

    public void setBenhNhanId(String benhNhanId) {
        this.benhNhanId = ObjectIdUtil.stringToId(benhNhanId);
    }

    public String getCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(coSoKhamBenhId);
    }
    
    public void setCoSoKhamBenhId(String coSoKhamBenhId) {
        this.coSoKhamBenhId = ObjectIdUtil.stringToId(coSoKhamBenhId);
    }
}
