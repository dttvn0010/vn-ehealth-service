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
@Document(collection = "emr_qua_trinh_cham_soc")
public class EmrQuaTrinhChamSoc {
    
    @Id public ObjectId id;    
    public ObjectId emrChamSocId;    
    public ObjectId emrVaoKhoaId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaychamsoc;

    public String ytachamsoc;

    public String theodoidienbien;

    public String thuchienylenh;
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getEmrChamSocId() {
        return ObjectIdUtil.idToString(emrChamSocId);
    }

    public void setEmrChamSocId(String emrChamSocId) {
        this.emrChamSocId = ObjectIdUtil.stringToId(emrChamSocId);
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
