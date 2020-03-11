package vn.ehealth.emr.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_yhct_don_thuoc_chi_tiet")
public class EmrYhctDonThuocChiTiet {
    
    @Id public ObjectId id;    
    public ObjectId emrYhctDonThuocId;    
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public EmrDmContent emrDmYhctViThuoc;    
    public EmrDmContent emrDmDuongDungThuoc;
    public EmrDmContent emrDmChiDanDungThuoc;
    public EmrDmContent emrDmTanXuatDungThuoc;
    
    public Date ngaybatdau;
    public Date ngayketthuc;
    public String lieuluongdung;
    public String chidandungthuoc;  
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getEmrYhctDonThuocId() {
        return ObjectIdUtil.idToString(emrYhctDonThuocId);
    }

    public void setEmrYhctDonThuocId(String emrYhctDonThuocId) {
        this.emrYhctDonThuocId = ObjectIdUtil.stringToId(emrYhctDonThuocId);
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
