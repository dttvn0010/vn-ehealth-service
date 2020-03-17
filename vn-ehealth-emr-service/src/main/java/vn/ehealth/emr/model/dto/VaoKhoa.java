package vn.ehealth.emr.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterLocationEntity;

public class VaoKhoa {
    public String locationId;
    public DanhMuc dmKhoaDieuTri;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public VaoKhoa() {
        super();
    }
    
    public VaoKhoa(EncounterLocationEntity ent) {
        if(ent == null || ent.location == null) return;
        
        var locationEnt = ServiceFactory.getLocationService().getByFhirId(ent.location.reference).orElseThrow();
        this.locationId = locationEnt.fhirId;
        var khoaDieuTri = KhoaDieuTri.fromEntity(locationEnt);
        if(khoaDieuTri != null) {
            this.dmKhoaDieuTri = khoaDieuTri.dmLoaiKhoa;
        }
        
        this.ngayGioVao = ent.getStart();
        this.ngayGioKetThucDieuTri = ent.getEnd();        
    }
    
    public static VaoKhoa fromEntity(EncounterLocationEntity ent) {
        if(ent == null) return null;
        return new VaoKhoa(ent);
    }
    
    public static EncounterLocationEntity toEntity(VaoKhoa dto) {
        if(dto == null) return null;
        var ent = new EncounterLocationEntity();
        ent.location = new BaseReference(dto.locationId);
        ent.period = new BasePeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri);
        return ent;
    }
    
}
