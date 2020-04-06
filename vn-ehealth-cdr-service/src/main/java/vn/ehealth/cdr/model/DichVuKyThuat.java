package vn.ehealth.cdr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import vn.ehealth.utils.MongoUtils;

public abstract class DichVuKyThuat {
	
    public String idhis;
    
	public DanhMuc dmKhoaDieuTri;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
    
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao; 
    
    public String ketLuan;
    
    abstract public CodeableConcept getCategory();
    
    abstract public CodeableConcept getCode();
    
	private Organization getKhoaDieuTri(Reference cskbRef, String maKhoa) {
	    if(cskbRef != null && cskbRef.hasReference()) {
	        var params = mapOf(
	                        "partOf.reference", (Object) cskbRef.getReference(),
	                        "type.coding.system", CodeSystemValue.KHOA_DIEU_TRI,
	                        "type.coding.code", dmKhoaDieuTri.ma
                        );
	        var criteria = MongoUtils.createCriteria(params);
	        return DaoFactory.getOrganizationDao().getResource(criteria);
	    }
	    return null;	    
	}
	
	private Encounter getVaoKhoaEncounter(Encounter hsbaEncounter) {
		if(hsbaEncounter == null 
			|| dmKhoaDieuTri == null) {
			return null;
		}		
		var khoaDieuTri = getKhoaDieuTri(hsbaEncounter.getServiceProvider(), dmKhoaDieuTri.ma);
		
		if(khoaDieuTri != null) {
		    var parent = (Object) (ResourceType.Encounter + "/" + hsbaEncounter.getId());
		    var params = mapOf("partOf.reference", parent);
			var criteria = MongoUtils.createCriteria(params);
			return DaoFactory.getEncounterDao().getResource(criteria);
		}
    	return null;
	}
	
	protected Map<String, Object> toFhirCommon(Encounter enc) {
	    
        if(enc != null) {
            var vkEnc = getVaoKhoaEncounter(enc);
            if(vkEnc != null) enc = vkEnc;
        }
        
        if(enc == null) new HashMap<>();
        
        var procedure = new Procedure();
        var serviceRequest = new ServiceRequest();
        var diagnosticReport = new DiagnosticReport();
        
        var code = getCode();
        var category = getCategory();
        var subject = enc.getSubject();
        var encounter = FhirUtil.createReference(enc);
        
        // Procedure
        
        String loaiDVKT = "";
        if(category != null && category.hasCoding()) {
            loaiDVKT = category.getCodingFirstRep().getCode();
        }
        
        if(!StringUtils.isBlank(idhis) && !StringUtils.isBlank(loaiDVKT)) {
            var identifier = loaiDVKT + "/" + idhis;
            procedure.setIdentifier(listOf(FhirUtil.createIdentifier(identifier, "")));
        }
        
        procedure.setCategory(category);
        procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
        
        // Service request
        serviceRequest.setCode(code);
        serviceRequest.setCategory(listOf(category));
        serviceRequest.setSubject(subject);
        serviceRequest.setEncounter(encounter);            
        serviceRequest.setAuthoredOn(ngayYeuCau);
        serviceRequest.setRequester(CanboYte.toRef(bacSiYeuCau));
        serviceRequest.setOrderDetail(listOf(FhirUtil.createCodeableConcept(noiDungYeuCau)));
        
        // Diagnostic report
        diagnosticReport.setCode(code);
        diagnosticReport.setCategory(listOf(category));
        diagnosticReport.setSubject(subject);
        diagnosticReport.setEncounter(encounter);
        diagnosticReport.setPerformer(listOf(CanboYte.toRef(nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(CanboYte.toRef(nguoiDanhGiaKetQua)));            
        diagnosticReport.setIssued(ngayGioBaoCao);
        diagnosticReport.setConclusion(ketLuan);
        
        return mapOf(
                "serviceRequest", serviceRequest,
                "procedure", procedure,
                "diagnosticReport", diagnosticReport
             );
    }
	
	abstract public Map<String, Object> toFhir(Encounter enc);
}
