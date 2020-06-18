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
@Document(collection = "ylenh")
public class Ylenh {
    
    @Id public ObjectId id;    
    
    public EmrRef hoSoBenhAnRef;    
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    
    public int trangThai;
    public String idhis;
    
    public DanhMuc dmLoaiYlenh;
    public DanhMuc dmLoaiDVKT;
    public DanhMuc dmNhomDVKT;
    
    public CanboYteDTO bacSiRaYlenh;
    
    public DanhMuc dmMaBenhChanDoan;
    public List<DanhMuc> dsDmMaBenhChanDoanKemTheo = new ArrayList<>();
    public String moTaChanDoan;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayRaYlenh;    
    
    public String hienThi;
    public String phong = "304";
    public String ghiChu;
        
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }    

    public static EmrRef toEmrRef(Ylenh obj) {
        if(obj == null || obj.id == null) {
            return null;
        }
        
        var ref = new EmrRef();
        ref.className = Ylenh.class.toString();
        ref.objectId = obj.id;
        ref.name = obj.hienThi;
        return ref;
    }
}
