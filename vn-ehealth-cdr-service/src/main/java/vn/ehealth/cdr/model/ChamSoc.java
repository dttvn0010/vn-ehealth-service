package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "cham_soc")
public class ChamSoc {
    @Id public ObjectId id;       
    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    
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
}
