package vn.ehealth.cdr.model;

import java.util.Date;

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
    
    public DanhMuc dmLoaiYlenh;
    public CanboYteDTO bacSiThucHien;
    
    public String display;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;    
        
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
        ref.objectId = obj.getId();
        ref.name = obj.display;
        return ref;
    }
}
