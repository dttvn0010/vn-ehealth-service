package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.MongoUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_hinh_anh_ton_thuong")
public class EmrHinhAnhTonThuong {
    
    @Id public ObjectId id;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    public String anhtonthuong;
    public String motatonthuong;
    public String dinhdanganh;
    
    public List<EmrFileDinhKem> emrFileDinhKemHatts = new ArrayList<>();
    
    public String getId() { 
        return MongoUtils.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = MongoUtils.stringToId(id);
    }

    public String getEmrHoSoBenhAnId() {
        return MongoUtils.idToString(emrHoSoBenhAnId);
    }
    
    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = MongoUtils.stringToId(emrHoSoBenhAnId);            
    }

    public String getEmrBenhNhanId() {
        return MongoUtils.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = MongoUtils.stringToId(emrBenhNhanId);
    }  
    
    public String getEmrCoSoKhamBenhId() {
        return MongoUtils.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = MongoUtils.stringToId(emrCoSoKhamBenhId);
    }
}
