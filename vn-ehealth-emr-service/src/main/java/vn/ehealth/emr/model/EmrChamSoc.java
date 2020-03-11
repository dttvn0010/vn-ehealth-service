package vn.ehealth.emr.model;

import java.util.ArrayList;
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
@Document(collection = "emr_cham_soc")
public class EmrChamSoc {
    
    @Id public ObjectId id;    
    public ObjectId emrVaoKhoaId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    @Transient public EmrVaoKhoa emrVaoKhoa;

    public String sotochamsoc;
    
    @Transient public List<EmrQuaTrinhChamSoc> emrQuaTrinhChamSocs;
    
    public List<EmrFileDinhKem> emrFileDinhKemChamSocs = new ArrayList<>();
    
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

    public String getEmrVaoKhoaId() {
        return ObjectIdUtil.idToString(emrVaoKhoaId);
    }

    public void setEmrVaoKhoaId(String emrVaoKhoaId) {
        this.emrVaoKhoaId = ObjectIdUtil.stringToId(emrVaoKhoaId);
    }    
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
    
    public List<EmrQuaTrinhChamSoc> getEmrQuaTrinhChamSocs() {
        if(emrQuaTrinhChamSocs == null && id != null) {
            emrQuaTrinhChamSocs = EmrServiceFactory.getEmrQuaTrinhChamSocService().getByEmrChamSocId(id);
        }
        return emrQuaTrinhChamSocs;
    }
}
