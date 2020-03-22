package vn.ehealth.emr.model.dto;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import java.util.Date;

import org.hl7.fhir.r4.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

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
    
    public VaoKhoa(EncounterLocationComponent ent) {
        if(ent == null || !ent.hasLocation()) return;
        this.locationId = ent.getLocation().getReference();
        var location =  DaoFactory.getLocationDao().read(createIdType(this.locationId));
        
        var orgRef = location.getManagingOrganization();
        var org = DaoFactory.getOrganizationDao().read(createIdType(orgRef));
        var khoaDieuTri = KhoaDieuTri.fromFhir(org);
        if(khoaDieuTri != null) {
            this.dmKhoaDieuTri = khoaDieuTri.dmLoaiKhoa;
        }
        
        this.ngayGioVao = ent.hasPeriod()? ent.getPeriod().getStart() : null;
        this.ngayGioKetThucDieuTri = ent.hasPeriod()? ent.getPeriod().getEnd() : null;        
    }
    
    public static VaoKhoa fromFhir(EncounterLocationComponent ent) {
        if(ent == null) return null;
        return new VaoKhoa(ent);
    }
    
    public static EncounterLocationComponent toFhir(VaoKhoa dto) {
        if(dto == null) return null;
        var ent = new EncounterLocationComponent();
        ent.setLocation(new Reference(dto.locationId));
        ent.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        return ent;
    }
    
}
