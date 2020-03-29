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

import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.model.dto.PhauThuatThuThuat;
import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_phau_thuat_thu_thuat")
public class EmrPhauThuatThuThuat extends EmrDichVuKyThuat {

    @Id public ObjectId id;        
    public ObjectId emrHoSoBenhAnId;  
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai; 
    public String idhis;
    
    public List<EmrDmContent> emrDmMaBenhChandoansaus = new ArrayList<>();
    public List<EmrDmContent> emrDmMaBenhChandoantruocs = new ArrayList<>();
    
    public EmrDmContent emrDmMaBenhChandoansau;
    public EmrDmContent emrDmMaBenhChandoantruoc;
    
    public EmrDmContent emrDmPhauThuThuat;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayyeucau;
    
    public EmrCanboYte bacsiyeucau;
    public String noidungyeucau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiopttt;
    public EmrCanboYte bacsithuchien;
    public EmrCanboYte bacsygayme;
    
    public String chidinhptt;
    public String phuongphapvocam;
    public String luocdoptt;
    public String trinhtuptt;
    
    public String motachandoantruocpt;
    public String motachandoansaupt;
        
    public List<EmrFileDinhKem> emrFileDinhKemPttts = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class EmrThanhVienPttt {

        public EmrDmContent emrDmVaiTro;        
        public EmrCanboYte bacsipttt;

    }
    
    public List<EmrThanhVienPttt> emrThanhVienPttts = new ArrayList<>();
    
    
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

    @Override
    public DichVuKyThuat toDto() {
        var dto = new PhauThuatThuThuat();
        dto.dmPttt = this.emrDmPhauThuThuat != null? this.emrDmPhauThuThuat.toDto() : null;
        dto.ngayYeuCau = this.ngayyeucau;
        
        if(this.bacsiyeucau != null) {
            dto.bacSiYeuCau = this.bacsiyeucau.toRef();
        }
        
        dto.noiDungYeuCau = this.noidungyeucau;
        
        dto.ngayThucHien = this.ngaygiopttt;
        
        if(this.bacsithuchien != null) {
            dto.chuTichHoiDong = this.bacsithuchien.toRef();
        }
        
        if(this.emrThanhVienPttts != null) {
            dto.hoiDongPttt = new ArrayList<>();
            for(var tvpttt : this.emrThanhVienPttts) {
                var tvdto = new PhauThuatThuThuat.ThanhVienPttt();
                tvdto.bacsi = tvpttt.bacsipttt != null? tvpttt.bacsipttt.toRef() : null;
                tvdto.dmVaiTro = tvpttt.emrDmVaiTro != null? tvpttt.emrDmVaiTro.toDto() : null;
                dto.hoiDongPttt.add(tvdto);
            }
        }
        
        dto.trinhTuPttt = this.trinhtuptt;
        dto.chiDinhPttt = this.chidinhptt;
        return dto;
    }
}
