package vn.ehealth.cdr.model;

import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.service.ServiceFactory;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdentifier;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createPeriod;

@JsonInclude(Include.NON_NULL)
@Document(collection = "ho_so_benh_an")
public class HoSoBenhAn {    
    
    @JsonInclude(Include.NON_NULL)
    public static class QuanLyNguoiBenh {

        public DanhMuc dmNoiGioiThieu;
        public DanhMuc dmLoaiDoiTuongTaiChinh;
        public DanhMuc dmLoaiRaVien;
        public DanhMuc dmNoiTrucTiepVao;
        public DanhMuc dmLoaiChuyenVien;
        public DanhMuc dmCoSoKhamBenh;
        public DanhMuc dmLoaiVaoVien;        
        public String soVaoVien;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayGioVaoVien;
        
        public Integer vaoVienLanThu;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayGioRaVien;
        
        public CanboYteDTO bacSiKham;
        public Integer tongSoNgayDieuTri;
        public CanboYteDTO bacSiChoRaVien;
        public String noiChuyenDen;
        
    }
    
    @JsonInclude(Include.NON_NULL)
    public static class TinhTrangRaVien {

        public DanhMuc dmMaBenhNguyenNhanTuVong;        
        public DanhMuc dmKetQuaDieuTri;        
        public DanhMuc dmYhctKetQuaDieuTri;        
        public DanhMuc dmMaBenhGiaiPhauTuThi;            
        public DanhMuc dmKetQuaGiaiPhauBenh;      
        public DanhMuc dmLyDoTuVong;       
        public DanhMuc dmThoiDiemTuVong;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayGioTuVong;

        public Boolean khamNghiemTuThi;
        public String moTaNguyenNhanTuVong;
        public String moTaGiaiPhauTuThi;
    }
    
    @JsonInclude(Include.NON_NULL)
    public static class VaoKhoa {

        public DanhMuc dmKhoaDieuTri;
        
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayGioVaoKhoa;

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        public Date ngayKetThucDieuTri;

        public String tenKhoa;

        public CanboYteDTO bacSiDieuTri;

        public String phong;

        public String giuong;

        public Integer soNgayDieuTri;

        public String tenTruongKhoa;
    }
    
    @JsonInclude(Include.NON_NULL)
    public static class ChanDoan {
        public DanhMuc dmMaBenhChanDoanNoiDen;
        public String moTaChanDoanNoiDen;
        
        public DanhMuc dmMaBenhChanDoanKkb;
        public String moTaChanDoanKkb;
        
        public DanhMuc dmMaBenhChanDoanDieuTriChinh;
        public String moTaChanDoanDieuTriChinh;
        
        public List<DanhMuc> dsDmMaBenhChanDoanDieuTriKemTheo = new ArrayList<>();
        public String moTaChanDoanDieuTriKemTheo;
        
        public DanhMuc dmMaBenhChanDoanDieuTriPhanBiet;
        public String moTaChanDoanDieuTriPhanBiet;
        
        public DanhMuc dmMaBenhChanDoanRaVienChinh;
        public String moTaChanDoanRaVienChinh;
                
        public List<DanhMuc> dsDmMaBenhChanDoanRaVienKemTheo = new ArrayList<>();
        public String moTaChanDoanRaVienKemTheo;
        
        public DanhMuc dmMaBenhChanDoanRaVienNguyenNhan;
        public String moTaChanDoanRaVienNguyenNhan;
        
        public DanhMuc dmMaBenhChanDoanTruocPttt;
        public String moTaChanDoanTruocPttt;
        
        public DanhMuc dmMaBenhChanDoanSauPttt;
        public String moTaChanDoanSauPttt;
    }
    
    @JsonInclude(Include.NON_NULL)    
    public static class ToDieuTri {
        public String ma;
        public String ten;
        public int soLuong;
    }
    
    
    @Id public ObjectId id;
            
    public DanhMuc dmLoaiBenhAn;   
    
    public EmrRef benhNhanRef;    
    @Transient public BenhNhan benhNhan;
    
    public EmrRef coSoKhamBenhRef;
    @Transient public CoSoKhamBenh coSoKhamBenh;       
    
    public int nguonDuLieu;    
    public int trangThai;
    public String maYte;
    public String maLuuTru;
    public String maTraoDoi;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayTiepNhan;
    
    public String nguoiTiepNhan;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayTao;
    
    public EmrRef nguoiTaoRef;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaySua;
    
    public EmrRef nguoiSuaRef;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayLuuTru;
    
    public EmrRef nguoiLuuTruRef;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayMoLuuTru;
    
    public EmrRef nguoiMoLuTruRef;    
    
    public EmrRef nguoiXoaRef;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayXoa;
    
    public QuanLyNguoiBenh quanLyNguoiBenh;
    
    public TinhTrangRaVien tinhTrangRaVien;
    
    public Map<String, Object> benhAn;
        
    public List<VaoKhoa> dsVaoKhoa = new ArrayList<>();
    
    public ChanDoan chanDoan;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayKyBenhAn;
    
    public CanboYteDTO bacSyLamBenhAn;
    
    public String nguoiGiaoHoSo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGiaoHoSo;

    public String nguoiNhanHoSo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaynhanhoso;

    public CanboYteDTO bacSyDieuTri;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaybacSyDieuTriky;
    
    public List<ToDieuTri> soToDieuTri = new ArrayList<>();
    
    public List<FileDinhKem> dsFileDinhKem = new ArrayList<>();
        
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }
       
    @JsonInclude(Include.NON_NULL)
    public static class TuoiBenhNhan {
        final public static int YEAR = 1;
        final public static int MONTH = 2;
        final public static int DAY = 3;
        
        public Integer tuoi;
        public Integer donVi;
        
        public TuoiBenhNhan() {
        }
        
        public TuoiBenhNhan(int tuoi, int donvi) {
            this.tuoi = tuoi;
            this.donVi = donvi;                
        }
    }
    
    public TuoiBenhNhan getTuoiBenhNhan() {
        var benhNhan = ServiceFactory.getBenhNhanService().getById(EmrRef.toObjectId(benhNhanRef)).orElse(null);
        
        if(benhNhan == null
            || benhNhan.ngaysinh == null
            || quanLyNguoiBenh == null 
            || quanLyNguoiBenh.ngayGioRaVien == null) {
            return new TuoiBenhNhan();
        }
        
        var d1 = new java.sql.Date(benhNhan.ngaysinh.getTime()).toLocalDate();
        var d2 = new java.sql.Date(quanLyNguoiBenh.ngayGioRaVien.getTime()).toLocalDate();
        
        var m1 = YearMonth.from(d1);
        var m2 = YearMonth.from(d2);
        
        var y1 = Year.from(d1);
        var y2 = Year.from(d2);
        
        int days = (int) d1.until(d2, ChronoUnit.DAYS);
        int months = (int) m1.until(m2, ChronoUnit.MONTHS);
        int years = (int) y1.until(y2, ChronoUnit.YEARS);
        
        if(d1.getDayOfMonth() > d2.getDayOfMonth()) 
            months -= 1;
        
        if(d1.getMonthValue() > d2.getMonthValue() || (d1.getMonthValue() == d2.getMonthValue() && d1.getDayOfMonth() > d2.getDayOfMonth())) 
            years -= 1;
        
        if(months < 1) {
            return new TuoiBenhNhan(days, TuoiBenhNhan.DAY);
        }else if(months < 36) {
            return new TuoiBenhNhan(months, TuoiBenhNhan.MONTH);
        }else {            
            return new TuoiBenhNhan(years, TuoiBenhNhan.YEAR);
        }
    }

    public String getKhoaRaVien() {
        
        if(dsVaoKhoa != null && dsVaoKhoa.size() > 0) {
            var khoaRaVien = dsVaoKhoa.get(dsVaoKhoa.size() - 1);
            var tenKhoaRaVien = khoaRaVien.tenKhoa;
            if(StringUtils.isEmpty(tenKhoaRaVien) && khoaRaVien.dmKhoaDieuTri != null) {
                tenKhoaRaVien = khoaRaVien.dmKhoaDieuTri.ten;
            }
            return tenKhoaRaVien;
        }
        
        return "";
    }
    
    public Encounter toFhir(Patient patient, Organization serviceProvider) {
        if(patient == null || serviceProvider == null) return null;
        
        var enc = new Encounter();
        enc.setIdentifier(listOf(createIdentifier(maYte, IdentifierSystem.MEDICAL_RECORD)));        
        enc.setSubject(FhirUtil.createReference(patient));
        enc.setServiceProvider(FhirUtil.createReference(serviceProvider));        
        
        if(quanLyNguoiBenh != null) {
            enc.setPeriod(createPeriod(quanLyNguoiBenh.ngayGioVaoVien, quanLyNguoiBenh.ngayGioRaVien));
        }
        
        return enc;
    }
    
    public static EmrRef toEmrRef(HoSoBenhAn obj) {
        if(obj == null || obj.id == null) {
            return null;
        }
        
        var ref = new EmrRef();
        ref.className = HoSoBenhAn.class.getName();
        ref.objectId = obj.id;
        ref.identifier = obj.maYte;
        return ref;
    }
}
