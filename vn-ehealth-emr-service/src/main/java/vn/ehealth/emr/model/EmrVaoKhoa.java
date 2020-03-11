package vn.ehealth.emr.model;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection="emr_vao_khoa")
public class EmrVaoKhoa {

    @Id public ObjectId id;
        
    public ObjectId emrHoSoBenhAnId;
    
    public ObjectId emrBenhNhanId;
    
    public ObjectId emrCoSoKhamBenhId;

    public EmrDmContent emrDmKhoaDieuTri;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaygiovaokhoa;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngayketthucdieutri;

    public String tenkhoa;

    public String bacsidieutri;

    public String phong;

    public String giuong;

    public Integer songaydieutri;

    public String tentruongkhoa;
    
    @Transient public List<EmrHoiChan> emrHoiChans;
    
    @Transient public List<EmrChamSoc> emrChamSocs;
    
    @Transient public List<EmrDieuTri> emrDieuTris;
    
    @Transient public List<EmrChucNangSong> emrChucNangSongs;
    
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
}
