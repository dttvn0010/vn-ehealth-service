package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.model.dto.XetNghiem;
import vn.ehealth.emr.utils.ObjectIdUtil;

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
    public static class EmrXetNghiemDichVu extends EmrDichVuKyThuat {
        
        @JsonIgnore
        public Date ngayyeucau;
        
        @JsonIgnore
        public EmrCanboYte bacsiyeucau;
        
        @JsonIgnore
        public String noidungyeucau;
        
        @JsonIgnore
        public Date ngaythuchien;
        
        @JsonIgnore
        public EmrCanboYte bacsixetnghiem;
                
        public EmrDmContent emrDmXetNghiem;
        public List<EmrXetNghiemKetQua> emrXetNghiemKetQuas = new ArrayList<EmrXetNghiemKetQua>();
        
        @JsonInclude(Include.NON_NULL)
        public static class EmrXetNghiemKetQua {
            public EmrDmContent emrDmDichKetQuaXetNghiem;
            
            public EmrDmContent emrDmChiSoXetNghiem;

            public String giatrido;
        }

        @Override
        public DichVuKyThuat toDto() {
            var dto = new XetNghiem();
            
            dto.dmXetNghiem = this.emrDmXetNghiem != null? this.emrDmXetNghiem.toDto() : null;
            dto.ngayYeuCau = this.ngayyeucau;
            
            if(this.bacsiyeucau != null) {
                dto.bacSiYeuCau = this.bacsiyeucau.toRef();
            }
            
            dto.noiDungYeuCau = this.noidungyeucau;
            
            dto.ngayThucHien = this.ngaythuchien;
            
            if(this.bacsixetnghiem != null) {
                dto.bacSiXetNghiem = this.bacsixetnghiem.toRef();
            }
            
            if(this.emrXetNghiemKetQuas != null) {
                dto.dsKetQuaXetNghiem = new ArrayList<>();
                for(var emrXnkq : this.emrXetNghiemKetQuas) {
                    var kqxn = new XetNghiem.KetQuaXetNghiem();
                    
                    if(emrXnkq.emrDmChiSoXetNghiem != null) {
                        kqxn.dmChiSoXetNghiem  = emrXnkq.emrDmChiSoXetNghiem.toDto();
                    }
                    
                    kqxn.giaTri = emrXnkq.giatrido;
                    dto.dsKetQuaXetNghiem.add(kqxn);
                }
            }
            return dto;
        } 
    }
    
    public List<EmrXetNghiemDichVu> emrXetNghiemDichVus = new ArrayList<>();
    
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
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
}
