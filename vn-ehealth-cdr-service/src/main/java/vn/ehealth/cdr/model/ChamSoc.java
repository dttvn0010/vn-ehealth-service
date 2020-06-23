package vn.ehealth.cdr.model;

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
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.service.ServiceFactory;
import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "cham_soc")
public class ChamSoc {
    @Id public ObjectId id;       
    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    
    public String idhis;
    public int trangThai;
    
    public CanboYteDTO ytaChamSoc;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayChamSoc;
    
    public DanhMuc dmLoaiChamSoc;
    public DanhMuc dmTriGiac;
    public Double nhietDo;
    public Double mach;
    public Double huyetApCao;
    public Double huyetApThap;
    public Double nhipTho;
    public Double spO2;
    
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
    
    @Transient public List<UongThuoc> dsUongThuoc;
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public List<UongThuoc> getDsUongThuoc() {
        if(dsUongThuoc == null) {
            dsUongThuoc = ServiceFactory.getUongThuocService().getByChamSocId(id);
        }
        return dsUongThuoc;
    }
    
    public static EmrRef toEmrRef(ChamSoc obj) {
        if(obj == null) return null;
        
        var ref = new EmrRef();
        ref.className = ChamSoc.class.getName();
        ref.objectId = obj.id;
        ref.identifier = obj.idhis;
        return ref;
    }
}
