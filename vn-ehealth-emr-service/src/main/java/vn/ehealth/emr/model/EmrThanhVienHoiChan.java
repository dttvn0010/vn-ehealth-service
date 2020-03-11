package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection="emr_thanh_vien_hoi_chan")
public class EmrThanhVienHoiChan {
    
    @Id public ObjectId id;    
    public ObjectId emrHoiChanId;    
    public ObjectId emrVaoKhoaId;
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public String tenbacsi;

    //public Integer idvaitro;
    
    public EmrDmContent emrDmVaiTro;
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getEmrHoiChanId() {
        return ObjectIdUtil.idToString(emrHoiChanId);
    }

    public void setHoiChanId(String emrHoiChanId) {
        this.emrHoiChanId = ObjectIdUtil.stringToId(emrHoiChanId);
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
