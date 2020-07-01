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
@Document(collection = "uong_thuoc")
public class UongThuoc {

    public static class VatTuYte {
        public DanhMuc dmVatTuYte;
        public int soLuong;
    }
    
    @Id public ObjectId id;       
    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef chamSocRef;
    public int trangThai;
    
    public DanhMuc dmThuoc;
    public DanhMuc dmDuongDungThuoc;
    public DanhMuc dmThoiDiemDungThuoc;
            
    public int soLuong;    
    public String donVi;
    public CanboYteDTO bacSiChiDinh;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayChiDinh;
    
    public CanboYteDTO ytaChamSoc;
    public List<VatTuYte> dsVatTuYte = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    } 
    
}
