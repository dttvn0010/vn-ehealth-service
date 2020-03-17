package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.emr.utils.FhirUtil.*;

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
    
    public DotKhamBenh(Encounter obj) {
        super(obj);
        
        if(obj == null) return;
        this.maYte = obj.hasIdentifier()? obj.getIdentifierFirstRep().getValue(): "";
        
        if(obj.hasType()) {
            var concept = findConceptBySystem(obj.getType(), CodeSystemValue.LOAI_KHAM_BENH);
            this.dmLoaiKham = new DanhMuc(concept);
        }
        
        if(obj.hasSubject())
            this.benhNhan = BenhNhan.fromReference(obj.getSubject());
        
        if(obj.hasServiceProvider())
            this.coSoKhamBenh = CoSoKhamBenh.fromReference(obj.getServiceProvider());
        
        if(obj.hasPeriod()) {
            this.ngayGioVao = obj.getPeriod().getStart();
            this.ngayGioKetThucDieuTri = obj.getPeriod().getEnd();
        }
        this.dsVaoKhoa = transform(obj.getLocation(), x -> VaoKhoa.fromFhir(x));        
    }
    
    public static DotKhamBenh fromFhir(Encounter obj) {
        if(obj == null) return null;
        return new DotKhamBenh(obj);
    }
    
    public static DotKhamBenh fromFhirId(String id) {
        if(id == null) return null;
        var ent = ServiceFactory.getEncounterService().getById(id);
        return fromFhir(ent);
    }
    
    public static DotKhamBenh fromReference(Reference ref) {
        if(ref == null) return null;
        return fromFhirId(ref.getReference());
    }
    
    public static Encounter toFhir(DotKhamBenh dto) {
        if(dto == null) return null;
        var ent = ServiceFactory.getEncounterService().getById(dto.id);
        
        if(ent == null) {
            ent = new Encounter();
            ent.setId(StringUtil.generateUID());
        }
        
        ent.setIdentifier(listOf(createIdentifier(dto.maYte, CodeSystemValue.DOT_KHAM_BENH)));
        ent.setType(listOf(DanhMuc.toConcept(dto.dmLoaiKham, CodeSystemValue.LOAI_KHAM_BENH)));
        ent.setSubject(BaseModelDTO.toReference(dto.benhNhan));
        ent.setServiceProvider(BaseModelDTO.toReference(dto.coSoKhamBenh));
        ent.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        ent.setLocation(transform(dto.dsVaoKhoa, x -> VaoKhoa.toFhir(x)));
        return ent;
    }
}
