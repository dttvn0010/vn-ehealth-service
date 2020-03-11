package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_phau_thuat_thu_thuat")
public class EmrPhauThuatThuThuat {

    @Id public ObjectId id;        
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public List<EmrDmContent> emrDmMaBenhChandoansaus = new ArrayList<>();
    public List<EmrDmContent> emrDmMaBenhChandoantruocs = new ArrayList<>();
    
    public EmrDmContent emrDmMaBenhChandoansau;
    public EmrDmContent emrDmMaBenhChandoantruoc;
    
    public EmrDmContent emrDmPhauThuThuat;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaygiopttt;
    public String bacsithuchien;
    public String bacsygayme;
    public String chidinhptt;
    public String phuongphapvocam;
    public String luocdoptt;
    public String trinhtuptt;
    
    public String motachandoantruocpt;
    public String motachandoansaupt;
    
    public Boolean loaipttt;
    //public String loaimoChklist;
        
    public List<EmrThanhVienPttt> emrThanhVienPttts = new ArrayList<>();
    
    public List<EmrFileDinhKem> emrFileDinhKemPttts = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }

    public String getEmrHoSoBenhAnId() {
        return ObjectIdUtil.idToString(emrHoSoBenhAnId);
    }
    
    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = ObjectIdUtil.stringToId(emrHoSoBenhAnId);            
    }

    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }   
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
    
    public EmrDmContent getEmrDmMaBenhChandoansau() {
        if(emrDmMaBenhChandoansaus != null && emrDmMaBenhChandoansaus.size() > 0) {
            return emrDmMaBenhChandoansaus.get(0);
        }
        
        return null;        
    }
    
    public EmrDmContent getemrDmMaBenhChandoantruoc() {
        if(emrDmMaBenhChandoantruocs != null && emrDmMaBenhChandoantruocs.size() > 0) {
            return emrDmMaBenhChandoantruocs.get(0);
        }
        return null;
    }
}
