package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.service.EmrServiceFactory;
import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_xet_nghiem")
public class EmrXetNghiem {
    
    @Id public ObjectId id;        
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public EmrDmContent emrDmXetNghiem;
    public EmrDmContent emrDmLoaiXetNghiem;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngayyeucau;
    
    public String bacsiyeucau;
    public String noidungyeucau;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaythuchien;
    
    public String bacsixetnghiem;    
    //public String tailieudinhkem;
    public String nhanxet;
   
    
    @Transient public List<EmrXetNghiemDichVu> emrXetNghiemDichVus = null;
    
    public List<EmrFileDinhKem> emrFileDinhKemXetNghiems = new ArrayList<>();
    
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
    
    public List<EmrXetNghiemDichVu> getEmrXetNghiemDichVus() {
        if(emrXetNghiemDichVus == null && id != null) {
            emrXetNghiemDichVus = EmrServiceFactory.getEmrXetNghiemDichVuService().getByEmrXetNghiemId(id);
        }
        return emrXetNghiemDichVus;
    }
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
}
