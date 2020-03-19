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

import vn.ehealth.emr.utils.MongoUtils;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_xet_nghiem")
public class EmrXetNghiem {
    
    @Id public ObjectId id;        
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    public EmrDmContent emrDmLoaiXetNghiem;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayyeucau;
    
    public EmrCanboYte bacsiyeucau;
    public String noidungyeucau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaythuchien;
    
    public EmrCanboYte bacsixetnghiem;    
    public String nhanxet;
    
    public List<EmrFileDinhKem> emrFileDinhKemXetNghiems = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class EmrXetNghiemDichVu {
        
        public EmrDmContent emrDmXetNghiem;
        public List<EmrXetNghiemKetQua> emrXetNghiemKetQuas = new ArrayList<EmrXetNghiemKetQua>();
        
        @JsonInclude(Include.NON_NULL)
        public static class EmrXetNghiemKetQua {
            public EmrDmContent emrDmDichKetQuaXetNghiem;
            
            public EmrDmContent emrDmChiSoXetNghiem;

            public String giatrido;
        }
          
    }
    
    public List<EmrXetNghiemDichVu> emrXetNghiemDichVus = new ArrayList<>();
    
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
