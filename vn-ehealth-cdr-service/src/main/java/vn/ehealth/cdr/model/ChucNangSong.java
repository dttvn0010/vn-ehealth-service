package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "chuc_nang_song")
public class ChucNangSong {
    
    @Id public ObjectId id;        
    public ObjectId hoSoBenhAnId;    
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    public DanhMuc dmKhoaDieuTri;
    
    public String soPhieu;
    
    public List<FileDinhKem> dsFileDinhKemChucNangSong = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class ChucNangSongChiTiet {
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayTheoDoi;
        public Double mach;
        public Double nhietDo;
        public Integer huyetApThap;
        public Integer huyetApCao;
        public Integer nhipTho;
        public Double canNang;
        public CanboYte ytaTheoDoi;
    }
    
    public List<ChucNangSongChiTiet> dsChucNangSongChiTiet = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getHoSoBenhAnId() {
        return ObjectIdUtil.idToString(hoSoBenhAnId);
    }
    
    public void setHoSoBenhAnId(String hoSoBenhAnId) {
        this.hoSoBenhAnId = ObjectIdUtil.stringToId(hoSoBenhAnId);            
    }

    public String getBenhNhanId() {
        return ObjectIdUtil.idToString(benhNhanId);
    }

    public void setBenhNhanId(String benhNhanId) {
        this.benhNhanId = ObjectIdUtil.stringToId(benhNhanId);
    }
    
    public String getCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(coSoKhamBenhId);
    }
    
    public void setCoSoKhamBenhId(String coSoKhamBenhId) {
        this.coSoKhamBenhId = ObjectIdUtil.stringToId(coSoKhamBenhId);
    }
}
