package vn.ehealth.emr.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.service.EmrServiceFactory;
import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_xet_nghiem_dich_vu")
public class EmrXetNghiemDichVu {
    
    @Id public ObjectId id;    
    public ObjectId emrXetNghiemId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public EmrDmContent emrDmXetNghiem;
    
    @Transient public List<EmrXetNghiemKetQua> emrXetNghiemKetQuas;
    
    public String getId() {
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
        
    public String getEmrXetNghiemId() {
        return ObjectIdUtil.idToString(emrXetNghiemId);
    }

    public void setEmrXetNghiemId(String emrXetNghiemId) {
        this.emrXetNghiemId = ObjectIdUtil.stringToId(emrXetNghiemId);
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
    
    public List<EmrXetNghiemKetQua> getEmrXetNghiemKetQuas() {
        if(emrXetNghiemKetQuas == null && id != null) {
            emrXetNghiemKetQuas = EmrServiceFactory.getEmrXetNghiemKetQuaService().getByEmrXetNghiemDichVuId(id);
        }
        
        return emrXetNghiemKetQuas;
    }
      
}
