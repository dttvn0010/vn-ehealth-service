package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class KhoaDieuTri extends BaseModelDTO {

    public String ma;
    public String ten;
    public DanhMuc dmLoaiKhoa;
    
    public KhoaDieuTri() {
        super();
    }
    
    public KhoaDieuTri(LocationEntity ent) {
        super(ent);
        if(ent == null) return;
        
        this.ma = ent.getIdentifier();
        this.ten = ent.name;
        this.dmLoaiKhoa = new DanhMuc(ent.getTypeBySystem(CodeSystem.KHOA_DIEU_TRI));
    }
    
    public static KhoaDieuTri fromEntity(LocationEntity ent) {
        if(ent == null) return null;
        return new KhoaDieuTri(ent);        
    }
    
    public static KhoaDieuTri fromReference(BaseReference ref) {
        if(ref != null && ref.reference != null) {
            var ent = ServiceFactory.getLocationService().getByFhirId(ref.reference).orElseThrow();
            return fromEntity(ent);
        }
        return null;        
    }
    
    public static LocationEntity toEntity(KhoaDieuTri dto) {
        if(dto == null) return null;
        var ent = ServiceFactory.getLocationService().getByFhirId(dto.fhirId).orElse(null);
        
        if(ent == null) {
            ent = new LocationEntity();
            ent.fhirId = ent.fhirId = StringUtil.generateUID();
        }
        
        ent.identifier = listOf(new BaseIdentifier(dto.ma, CodeSystem.CO_SO_KHAM_BENH));
        ent.name = dto.ten;
        ent.type = listOf(DanhMuc.toBaseCodeableConcept(dto.dmLoaiKhoa, CodeSystem.KHOA_DIEU_TRI));
        return ent;
    }
}
