package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
    
    public List<String> dsChiDinhThuocId = new ArrayList<>();
    
    public List<DanhMuc> dsDmVatTuYTe = new ArrayList<>();
}
