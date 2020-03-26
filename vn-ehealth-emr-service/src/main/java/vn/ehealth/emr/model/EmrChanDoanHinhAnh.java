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

import vn.ehealth.emr.model.dto.ChanDoanHinhAnh;
import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_chan_doan_hinh_anh")
public class EmrChanDoanHinhAnh extends EmrDichVuKyThuat {

    @Id public ObjectId id;    
    
    public ObjectId emrHoSoBenhAnId;
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
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
    
    public int trangThai;
    public String idhis;
    
    public EmrDmContent emrDmLoaiChanDoanHinhAnh;    
    public EmrDmContent emrDmChanDoanHinhAnh;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayyeucau;
    
    public EmrCanboYte bacsiyeucau;
    public String noidungyeucau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaythuchien;
    
    public EmrCanboYte bacsichuyenkhoa;
    public String ketqua;
    public String ketluan;
    public String loidan;
        
    public List<EmrFileDinhKem> emrFileDinhKemCdhas = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public ChanDoanHinhAnh toDto() {
    	var dto = new ChanDoanHinhAnh();
        dto.dmCdha = this.emrDmChanDoanHinhAnh != null? this.emrDmChanDoanHinhAnh.toDto() : null;
        dto.ngayYeuCau = this.ngayyeucau;
        dto.bacSiYeuCau = this.bacsiyeucau != null? this.bacsiyeucau.toRef() : null;
        dto.noiDungYeuCau = this.noidungyeucau;
        
        dto.ngayThucHien = this.ngaythuchien;
        dto.bacSiChuyenKhoa = this.bacsichuyenkhoa != null? this.bacsichuyenkhoa.toRef() : null;
        dto.ketQua = this.ketqua;
        dto.ketLuan = this.ketluan;
        dto.loiDan = this.loidan;
        return dto;
    }
}
