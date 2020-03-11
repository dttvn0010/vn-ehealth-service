package vn.ehealth.emr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import vn.ehealth.emr.service.EmrServiceFactory;
import vn.ehealth.emr.utils.JasperUtils;
import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_HOSO;

@JsonInclude(Include.NON_NULL)
@Document(collection="emr_ho_so_benh_an")
public class EmrHoSoBenhAn {    
    @Id public ObjectId id;
            
    public EmrDmContent emrDmLoaiBenhAn;
    
    public transient ObjectId emrBenhNhanId;
    @Transient public EmrBenhNhan emrBenhNhan;
    
    public transient ObjectId emrCoSoKhamBenhId;
    @Transient public EmrCoSoKhamBenh emrCoSoKhamBenh;
        
    public int nguonDuLieu;    
    public int trangThai;
    public String mayte;
    public String maluutru;
    public String matraodoi;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaytiepnhan;
    
    public String nguoitiepnhan;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaytao;
    
    public ObjectId nguoitaoId;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaysua;
    
    public ObjectId nguoisuaId;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngayluutru;
    
    public ObjectId nguoiluutruId;
    
    @JsonFormat(pattern="dd/MM/yyyy HH:mm")
    public Date ngaymoluutru;
    
    public ObjectId nguoimoluutruId;    
    
    public EmrQuanLyNguoiBenh emrQuanLyNguoiBenh;
    
    public EmrTongKetRaVien emrTongKetRaVien;
    
    public EmrTinhTrangRaVien emrTinhTrangRaVien;
    
    public EmrTongKetSanKhoa emrTongKetSanKhoa;  //??
    
    public EmrBenhAn emrBenhAn;
    
    public EmrYhctBenhAn emrYhctBenhAn;
    
    public EmrChanDoan emrChanDoan;
    
    public EmrYhctChanDoan emrYhctChanDoan;
    
    public EmrYhctNhaBa emrYhctNhaBa;
    
    public List<EmrYhctNhaBaGhiChu> emrYhctNhaBaGhiChus = new ArrayList<>();
    
    public List<EmrQuaTrinhSuDungThuoc> emrQuaTrinhSuDungThuocs = new ArrayList<>();
    
    public List<EmrFileDinhKem> emrFileDinhKems = new ArrayList<>();
        
    @Transient public List<EmrVaoKhoa> emrVaoKhoas;
    
    @Transient public List<EmrHinhAnhTonThuong> emrHinhAnhTonThuongs;
    
    @Transient public List<EmrGiaiPhauBenh> emrGiaiPhauBenhs;
    
    @Transient public List<EmrThamDoChucNang> emrThamDoChucNangs;
    
    @Transient public List<EmrPhauThuatThuThuat> emrPhauThuatThuThuats;
    
    @Transient  public List<EmrChanDoanHinhAnh> emrChanDoanHinhAnhs;
    
    @Transient  public List<EmrDonThuoc> emrDonThuocs;
    
    @Transient  public List<EmrYhctDonThuoc> emrYhctDonThuocs;
    
    @Transient  public List<EmrXetNghiem> emrXetNghiems;

    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public EmrDmContent getEmrDmLoaiBenhAn() {
        return emrDmLoaiBenhAn;
    }
    
    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }
    
    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }
    
    public EmrBenhNhan getEmrBenhNhan() {
        if(emrBenhNhan == null && emrBenhNhanId != null) {
            emrBenhNhan = EmrServiceFactory.getEmrBenhNhanService().getById(emrBenhNhanId).orElse(null);
        }
        return emrBenhNhan;
    }
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
    
    public EmrCoSoKhamBenh getEmrCoSoKhamBenh() {
        if(emrCoSoKhamBenh == null && emrCoSoKhamBenhId != null) {
            emrCoSoKhamBenh = EmrServiceFactory.getEmrCoSoKhamBenhService().getById(emrCoSoKhamBenhId).orElse(null);
        }
        return emrCoSoKhamBenh;
    }
    
    public String getNguoitaoId() {
        return ObjectIdUtil.idToString(nguoitaoId);
    }
    
    public void setNguoitaoId(String nguoitaoId) {
        this.nguoitaoId = ObjectIdUtil.stringToId(nguoitaoId);
    }
    
    public String getNguoisuaId() {
        return ObjectIdUtil.idToString(nguoisuaId);
    }
    
    public void setNguoisuaId(String nguoisuaId) {
        this.nguoitaoId = ObjectIdUtil.stringToId(nguoisuaId);
    }
    
    public String getNguoiluutruId() {
        return ObjectIdUtil.idToString(nguoiluutruId);
    }
    
    public void setNguoiluutruId(String nguoiluutruId) {
        this.nguoiluutruId = ObjectIdUtil.stringToId(nguoiluutruId);
    }
    
    public String getNguoimoluutruId() {
        return ObjectIdUtil.idToString(nguoimoluutruId);
    }
    
    public void setNguoimoluutruId(String nguoimoluutruId) {
        this.nguoimoluutruId = ObjectIdUtil.stringToId(nguoimoluutruId);
    }
    
    
    public String getMayte() {
        return mayte;
    }
    
    public String getMaluutru() {
        return maluutru;
    }
    
    public String getMatraodoi() {
        return matraodoi;
    }
    
    public String getGiamdocbenhvien() {
        return emrCoSoKhamBenh != null? emrCoSoKhamBenh.giamdoc : "";
    }
    
    public String getTenbenhvien() {
        getEmrCoSoKhamBenh();
        return emrCoSoKhamBenh != null? emrCoSoKhamBenh.ten : "";
    }
    
    public String getDonvichuquan() {
        return emrCoSoKhamBenh != null? emrCoSoKhamBenh.donvichuquan : "";
    }
    
    public String getTruongphongth() {
        return emrCoSoKhamBenh != null? emrCoSoKhamBenh.truongphongth : "";
    }
    
    public EmrQuanLyNguoiBenh getEmrQuanLyNguoiBenh() {
        return emrQuanLyNguoiBenh;
    }
    
    public EmrTongKetRaVien getEmrTongKetRaVien() {
        return emrTongKetRaVien;
    }
    
    public EmrTinhTrangRaVien getEmrTinhTrangRaVien() {
        return emrTinhTrangRaVien;
    }
    
    public EmrTongKetSanKhoa getEmrTongKetSanKhoa() {
        return emrTongKetSanKhoa;
    }
    
    public EmrBenhAn getEmrBenhAn() {
        return emrBenhAn;
    }
    
    public EmrYhctBenhAn getEmrYhctBenhAn() {
        return emrYhctBenhAn;
    }
    
    public EmrChanDoan getEmrChanDoan() {
        return emrChanDoan;
    }
    
    public EmrYhctChanDoan getEmrYhctChanDoan() {
        return emrYhctChanDoan;
    }    
    
    public EmrYhctNhaBa getEmrYhctNhaBa() {
        return emrYhctNhaBa;
    }
    
    public List<EmrYhctNhaBaGhiChu> getEmrYhctNhaBaGhiChus() {
        return emrYhctNhaBaGhiChus;
    }
    
    public List<EmrQuaTrinhSuDungThuoc> getEmrQuaTrinhSuDungThuocs() {
        return emrQuaTrinhSuDungThuocs;
    }
    
    public List<EmrFileDinhKem> getEmrFileDinhKems() {
        return emrFileDinhKems;
    }
    
    public EmrVaoKhoa[] getEmrVaoKhoas() {
        if(emrVaoKhoas == null) {
            emrVaoKhoas = EmrServiceFactory.getEmrVaoKhoaService().getByEmrHoSoBenhAnId(id);
        }
        return emrVaoKhoas.toArray(new EmrVaoKhoa[0]);        
    }
    
    public List<EmrHinhAnhTonThuong> getEmrHinhAnhTonThuongs() {
        return emrHinhAnhTonThuongs;
    }
    
    public List<EmrGiaiPhauBenh> getEmrGiaiPhauBenhs() {
        return emrGiaiPhauBenhs;
    }
    
    public List<EmrThamDoChucNang> getEmrThamDoChucNangs() {
        return emrThamDoChucNangs;
    }
    
    public List<EmrPhauThuatThuThuat> getEmrPhauThuatThuThuats() {
        return emrPhauThuatThuThuats;
    }
    
    public List<EmrChanDoanHinhAnh> getEmrChanDoanHinhAnhs() {
        return emrChanDoanHinhAnhs;
    }
    
    public List<EmrDonThuoc> getEmrDonThuocs() {
        return emrDonThuocs;
    }
    
    public List<EmrYhctDonThuoc> getEmrYhctDonThuocs() {
        return emrYhctDonThuocs;
    }
    
    public List<EmrXetNghiem> getEmrXetNghiems() {
        return emrXetNghiems;
    }
    
    public Boolean getCoPhauThuat() {
        return false;
    }
    
    public Boolean getCoThuThuat() {
        return false;
    }    
    
    public boolean getDaxoa() {
        return trangThai == TRANGTHAI_HOSO.DA_XOA;
    }
    
    public String getTuoiBenhNhan() {
        var tuoi = JasperUtils.getTuoi(this);
        var arr = tuoi.split(" ");
        return arr[0];
    }
    
    public String getDonViTuoiBenhNhan() {
        var tuoi = JasperUtils.getTuoi(this);
        var arr = tuoi.split(" ");
        return arr.length > 1? arr[1] : "";        
    }
    
    public String getKhoaRaVien() {
        var emrVaoKhoas = getEmrVaoKhoas();
        
        if(emrVaoKhoas != null && emrVaoKhoas.length > 0) {
            var emrKhoaRaVien = emrVaoKhoas[emrVaoKhoas.length - 1];
            var khoaRaVien = emrKhoaRaVien.tenkhoa;
            if(StringUtils.isEmpty(khoaRaVien) && emrKhoaRaVien.emrDmKhoaDieuTri != null) {
                khoaRaVien = emrKhoaRaVien.emrDmKhoaDieuTri.ten;
            }
            return khoaRaVien;
        }
        
        return "";
    }
    
    public String getTongketsankhoaCda() {
        return "";
    }
}
