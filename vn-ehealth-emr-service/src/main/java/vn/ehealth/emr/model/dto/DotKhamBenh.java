package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.ehr.entity.EncounterEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class DotKhamBenh extends BaseModelDTO {    
    public BenhNhan benhNhan;
    public CoSoKhamBenh coSoKhamBenh;
    public String maYte;
    public DanhMuc dmLoaiKham;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public List<VaoKhoa> dsVaoKhoa = new ArrayList<>();
    
    public DotKhamBenh() {
        super();
    }
    
    public DotKhamBenh(EncounterEntity ent) {
        super(ent);
        
        if(ent == null) return;
        this.maYte = ent.getIdentifier();
        this.dmLoaiKham = new DanhMuc(ent.getTypeBySystem(CodeSystem.LOAI_KHAM_BENH));
        this.benhNhan = BenhNhan.fromReference(ent.subject);
        this.coSoKhamBenh = CoSoKhamBenh.fromReference(ent.serviceProvider);
        this.ngayGioVao = ent.getStart();
        this.ngayGioKetThucDieuTri = ent.getEnd();
        this.dsVaoKhoa = DataConvertUtil.transform(ent.location, x -> VaoKhoa.fromEntity(x));        
    }
    
    public static DotKhamBenh fromEntity(EncounterEntity ent) {
        if(ent == null) return null;
        return new DotKhamBenh(ent);
    }
    
    public static DotKhamBenh fromFhirId(String fhirId) {
        if(fhirId == null) return null;
        var ent = ServiceFactory.getEncounterService().getByFhirId(fhirId).orElseThrow();
        return fromEntity(ent);
    }
    
    public static DotKhamBenh fromReference(BaseReference ref) {
        if(ref == null) return null;
        return fromFhirId(ref.reference);
    }
    
    public static EncounterEntity toEntity(DotKhamBenh dto) {
        if(dto == null) return null;
        var ent = ServiceFactory.getEncounterService().getByFhirId(dto.fhirId).orElse(null);
        
        if(ent == null) {
            ent = new EncounterEntity();
            ent.fhirId = ent.fhirId = StringUtil.generateUID();
        }
        
        ent.identifier = listOf(new BaseIdentifier(dto.maYte, CodeSystem.DOT_KHAM_BENH));
        ent.type = listOf(DanhMuc.toBaseCodeableConcept(dto.dmLoaiKham, CodeSystem.LOAI_KHAM_BENH));
        ent.subject = BaseModelDTO.toReference(dto.benhNhan);
        ent.serviceProvider = BaseModelDTO.toReference(dto.coSoKhamBenh);
        ent.period = new BasePeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri);
        ent.location = DataConvertUtil.transform(dto.dsVaoKhoa, x -> VaoKhoa.toEntity(x));
        return ent;
    }
}
