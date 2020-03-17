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
   
    public int trangThai;
    
    public EmrDmContent emrDmGioiTinh;
    
    public EmrDmContent emrDmDanToc;
    
    public EmrDmContent emrDmQuocGia;
    
    public EmrDmContent emrDmNgheNghiep;
    
    public EmrDmContent emrDmPhuongXa;
    
    public EmrDmContent emrDmQuanHuyen;
    
    public EmrDmContent emrDmTinhThanh;
    
    public EmrDmContent emrDmNgheNghiepBo;
    
    public EmrDmContent emrDmNgheNghiepMe;
    
    public EmrDmContent emrDmLoaiDoiTuongTaiChinh;
    
    public String iddinhdanhchinh;

    public String iddinhdanhphu;

    public String idhis;

    public String tendaydu;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysinh;

    public String diachi;

    public String noilamviec;

    public String sobhyt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayhethanthebhyt;

    public String hotenbo;

    public String hotenme;

    public String tennguoibaotin;

    public String diachinguoibaotin;

    public String sodienthoainguoibaotin;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaBo;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySinhCuaMe;
    
    public String trinhDoVanHoaCuaBo;
    
    public String trinhDoVanHoaCuaMe;
    
    public ObjectId emrPersonId;
        
}
