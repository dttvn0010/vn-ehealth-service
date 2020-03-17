package vn.ehealth.emr.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_person")
public class EmrPerson {
    
    @Id public ObjectId id;
    
    public String getId() { return id != null? id.toHexString() : null; }
    
    public String tendaydu;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaysinh;
    public String email;
    public String dienthoai;
    public String cmnd;
    public String noilamviec;
    public String diachiChitiet;

    public EmrDmContent emrDmGioiTinh;
    public EmrDmContent emrDmDanToc;
    public EmrDmContent emrDmQuocGia;
    public EmrDmContent emrDmNgheNghiep;
    public EmrDmContent emrDmPhuongXa;
    public EmrDmContent emrDmQuanHuyen;
    public EmrDmContent emrDmTinhThanh;
    
    public ObjectId userId;
}
