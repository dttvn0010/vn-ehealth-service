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

import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "dieu_tri")
public class DieuTri {

    public static class ChiDinh {

        public String anUong;
        public DanhMuc dmCheDoAnUong;
        public DanhMuc dmCheDoChamSoc;
        public DanhMuc dmCapHoLy;
        public DanhMuc dmLoaiToaThuoc;
    }
    
    @Id public ObjectId id;
    
    public EmrRef hoSoBenhAnRef;    
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef ylenhRef;
    public CanboYteDTO bacSiDieuTri;
    
    public int trangThai;
    
    public DanhMuc dmMaBenhChanDoan;
    public List<DanhMuc> dsDmMaBenhChanDoanKemTheo = new ArrayList<>();
    
    public String dienBien;
    public String dienBienQuanTrong;
    public String loiDan;
    public String ghiChu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayDieuTri;
    
    public ChiDinh chiDinh;
    
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }
}
