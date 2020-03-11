package vn.ehealth.emr.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_benh_nhan")
public class EmrBenhNhan {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }    
   
    public EmrDmContent emrDmGioiTinh;
    
    public EmrDmContent emrDmDanToc;
    
    public EmrDmContent emrDmQuocGia;
    
    public EmrDmContent emrDmNgheNghiep;
    
    public EmrDmContent emrDmPhuongXa;
    
    public EmrDmContent emrDmQuanHuyen;
    
    public EmrDmContent emrDmTinhThanh;
    
    public EmrDmContent emrDmNgheNghiepBo;
    
    public EmrDmContent emrDmNgheNghiepMe;
    
    public String iddinhdanhchinh;

    public String iddinhdanhphu;

    public String idhis;

    public String tendaydu;
    
    @JsonFormat(pattern="dd/MM/yyyy")
    public Date ngaysinh;

    public String diachi;

    public String noilamviec;

    public String sobhyt;

    @JsonFormat(pattern="dd/MM/yyyy")
    public Date ngayhethanthebhyt;

    public String hotenbo;

    public String hotenme;

    public String tennguoibaotin;

    public String diachinguoibaotin;

    public String sodienthoainguoibaotin;
    
    @JsonFormat(pattern="dd/MM/yyyy")
    public Date ngaySinhCuaBo;
    
    @JsonFormat(pattern="dd/MM/yyyy")
    public Date ngaySinhCuaMe;
    
    public String trinhDoVanHoaCuaBo;
    
    public String trinhDoVanHoaCuaMe;
        
}
