package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.service.ServiceFactory;
import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "don_thuoc")
public class DonThuoc {
    
    @Id public ObjectId id;    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef ylenhRef;
    
    public int trangThai;
    public String idhis;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayKeDon;
    public CanboYteDTO bacSiKeDon;
    public String soDon;
    
    @Transient public List<DonThuocChiTiet> dsDonThuocChiTiet;
    
    public List<FileDinhKem> dsFileDinhKemDonThuoc = new ArrayList<>();     
    
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public List<DonThuocChiTiet> getDsDonThuocChiTiet() {
        if(dsDonThuocChiTiet == null) {
            dsDonThuocChiTiet = ServiceFactory.getDonThuocChiTietService().getByDonThuocId(id);
        }
        return dsDonThuocChiTiet;
    }
    
    public static EmrRef toEmrRef(DonThuoc obj) {
        if(obj == null) return null;
        var ref = new EmrRef();
        ref.className = DonThuoc.class.getName();
        ref.objectId = obj.id;
        ref.identifier = obj.idhis;
        ref.code = obj.soDon;
        return ref;
    }
}
