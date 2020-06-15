package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "y_lenh")
public class Ylenh {
    
    @Id public ObjectId id;       
    public ObjectId hoSoBenhAnId;    
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    public int trangThai;
    
    public DanhMuc dmMaBenhChanDoan;
    public String dienBien;
    public String dienBienQuanTrong;
    public String loiDan;
    public String ghiChu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayDieuTri;
    public CanboYte bacSiRaYlenh;
    
    public static class ChiDinh {
        public String anUong;
        public DanhMuc dmCheDoAnUong;
        public DanhMuc dmCheDoChamSoc;
        public DanhMuc dmCapHoLy;
        public DanhMuc dmLoaiToaThuoc;
    }
    
    public ChiDinh chiDinh;
   
    
    public static List<ChiDinhThuoc> dsChiDinhThuoc = new ArrayList<>();
    
    public static List<ChiDinhDVKT> dsChiDinhDVKT = new ArrayList<>();
}
