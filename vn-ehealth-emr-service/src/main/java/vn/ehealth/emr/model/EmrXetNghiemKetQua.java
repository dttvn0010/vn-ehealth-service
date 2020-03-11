package vn.ehealth.emr.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_xet_nghiem_ket_qua")
public class EmrXetNghiemKetQua {
    
    @Id public ObjectId id;    
    public ObjectId emrXetNghiemDichVuId;    
    public ObjectId emrXetNghiemId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public EmrDmContent emrDmDichKetQuaXetNghiem;
    
    public EmrDmContent emrDmChiSoXetNghiem;

    public String giatrido;
    
    public String getId() {
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
            
    public String getEmrXetNghiemDichVuId() {
        return ObjectIdUtil.idToString(emrXetNghiemDichVuId);
    }

    public void setEmrXetNghiemDichVuId(String emrXetNghiemDichVuId) {
        this.emrXetNghiemDichVuId = ObjectIdUtil.stringToId(emrXetNghiemDichVuId);
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
}
