
package vn.ehealth.emr.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_chuc_nang_song_chi_tiet")
public class EmrChucNangSongChiTiet {
    
    @Id public ObjectId id;    
    public ObjectId emrChucNangSongId;    
    public ObjectId emrVaoKhoaId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaytheodoi;
    public Double mach;
    public Double nhietdo;
    public Integer huyetapthap;
    public Integer huyetapcao;
    public Integer nhiptho;
    public Double cannang;
    public String ytatheodoi;
    
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
        
    public String getEmrChucNangSongId() {
        return ObjectIdUtil.idToString(emrChucNangSongId);
    }

    public void setEmrChucNangSongId(String emrChucNangSongId) {
        this.emrChucNangSongId = ObjectIdUtil.stringToId(emrChucNangSongId);
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
    
    public String getEmrVaoKhoaId() {
        return ObjectIdUtil.idToString(emrVaoKhoaId);
    }

    public void setEmrVaoKhoaId(String emrVaoKhoaId) {
        this.emrVaoKhoaId = ObjectIdUtil.stringToId(emrVaoKhoaId);
    }
}
