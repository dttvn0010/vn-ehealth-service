package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;

public class KhoaDieuTri extends BaseModelDTO {

    public String ma;
    public String ten;
    public DanhMuc dmLoaiKhoa;
    
    public KhoaDieuTri() {
        super();
    }
    
    public KhoaDieuTri(Organization ent) {
        super(ent);
        if(ent == null) return;
        
        this.ma = ent.hasIdentifier()? ent.getIdentifierFirstRep().getValue() : "";
        this.ten = ent.hasName()? ent.getName() : "";
        this.dmLoaiKhoa = new DanhMuc(findConceptBySystem(ent.getType(), CodeSystemValue.KHOA_DIEU_TRI));
    }
    
    public static KhoaDieuTri fromFhir(Organization obj) {
        if(obj == null) return null;
        return new KhoaDieuTri(obj);        
    }
    
    public static KhoaDieuTri fromReference(Reference ref) {
        if(ref != null) {
            var obj = DaoFactory.getOrganizationDao().read(createIdType(ref));
            return fromFhir(obj);
        }
        return null;        
    }
    
    public static Organization toFhir(KhoaDieuTri dto) {
        if(dto == null) return null;
        var ent = new Organization();
        ent.setId(dto.id);
        ent.setIdentifier(listOf(createIdentifier(dto.ma, CodeSystemValue.CO_SO_KHAM_BENH)));
        ent.setName(dto.ten);
        ent.setType(listOf(DanhMuc.toConcept(dto.dmLoaiKhoa, CodeSystemValue.KHOA_DIEU_TRI)));
        return ent;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.DiagnosticReport;
    }
}
