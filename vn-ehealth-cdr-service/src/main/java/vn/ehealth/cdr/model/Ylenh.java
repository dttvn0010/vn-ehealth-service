package vn.ehealth.cdr.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;

@JsonInclude(Include.NON_NULL)
@Document(collection = "ylenh")
public class Ylenh {
    
    @Id public ObjectId id;    
    
    public EmrRef hoSoBenhAnRef;    
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public int trangThai;
    
    public DanhMuc dmLoaiYlenh;
    public EmrRef bacSiThucHienUserRef;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;    
}
