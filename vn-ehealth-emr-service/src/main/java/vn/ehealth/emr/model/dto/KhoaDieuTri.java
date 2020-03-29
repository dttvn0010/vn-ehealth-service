package vn.ehealth.emr.model.dto;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiToChuc;
import vn.ehealth.emr.utils.MessageUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

import org.hl7.fhir.r4.model.Organization;

public class KhoaDieuTri extends ToChuc {
    public BaseRef parent;
    public DanhMuc dmLoaiKhoa;
    
    public KhoaDieuTri() {
        super();
    }
    
    public KhoaDieuTri(Organization obj) {
        super(obj);
    }
    
    public void getContentFromFhir(Organization obj) {
        if(obj != null) {
            this.parent = new BaseRef(obj.getPartOf());
            this.ten = obj.hasName()? obj.getName() : "";
            this.dmLoaiKhoa = new DanhMuc(findConceptBySystem(obj.getType(), CodeSystemValue.KHOA_DIEU_TRI));
        }
    }
    
    public static KhoaDieuTri fromFhir(Organization obj) {
    	if(obj == null) return null;
    	return new KhoaDieuTri(obj);
    }
       
    public Organization toFhir() {
        var obj = new Organization();
        
        obj.setId(this.id);
        obj.setPartOf(BaseRef.toOrganizationRef(this.parent));
        obj.setName(this.ten);
        
        var orgType = createCodeableConcept(LoaiToChuc.KHOA_DIEU_TRI, 
                                            MessageUtils.get("text.FAL"), 
                                            CodeSystemValue.LOAI_TO_CHUC);
                                    
        var falType = DanhMuc.toConcept(this.dmLoaiKhoa, CodeSystemValue.KHOA_DIEU_TRI);
        
        obj.setType(listOf(orgType, falType));
        
        return obj;
    }
}
