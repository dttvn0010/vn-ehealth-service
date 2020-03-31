package vn.ehealth.emr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

public abstract class EmrDichVuKyThuat {
	
	public EmrDmContent emrDmKhoaDieuTri;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayyeucau;
    
    public EmrCanboYte bacsiyeucau;
    public String noidungyeucau;
    
    public EmrCanboYte nguoivietbaocao;
    public EmrCanboYte nguoidanhgiaketqua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiobaocao; 
    
    public String ketluan;
    
    abstract protected CodeableConcept getCategory();
    
    abstract protected CodeableConcept getCode();
    
	private Organization getKhoaDieuTri(Reference cskbRef, String maKhoa) {
	    if(cskbRef != null && cskbRef.hasReference()) {
	        var params = mapOf(
	                        "partOf.reference", (Object) cskbRef.getReference(),
	                        "type.coding.system", CodeSystemValue.KHOA_DIEU_TRI,
	                        "type.coding.code", emrDmKhoaDieuTri.ma
                        );
	        var criteria = MongoUtils.createCriteria(params);
	        return DaoFactory.getOrganizationDao().getResource(criteria);
	    }
	    return null;	    
	}
	
	private Encounter getVaoKhoaEncounter(Encounter hsbaEncounter) {
		if(hsbaEncounter == null 
			|| emrDmKhoaDieuTri == null) {
			return null;
		}		
		var khoaDieuTri = getKhoaDieuTri(hsbaEncounter.getServiceProvider(), emrDmKhoaDieuTri.ma);
		
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
        
        if(procedure != null) {
            procedure.setCategory(category);
            procedure.setCode(code);
            procedure.setSubject(subject);        
            procedure.setEncounter(encounter);
        }
        
        if(serviceRequest != null) {
            serviceRequest.setCode(code);
            serviceRequest.setCategory(listOf(category));
            serviceRequest.setSubject(subject);
            serviceRequest.setEncounter(encounter);            
            serviceRequest.setAuthoredOn(ngayyeucau);
            serviceRequest.setRequester(EmrCanboYte.toRef(bacsiyeucau));
            serviceRequest.setOrderDetail(listOf(FhirUtil.createCodeableConcept(noidungyeucau)));
        }
        
        if(diagnosticReport != null) {
            diagnosticReport.setCode(code);
            diagnosticReport.setCategory(listOf(category));
            diagnosticReport.setSubject(subject);
            diagnosticReport.setEncounter(encounter);
            diagnosticReport.setPerformer(listOf(EmrCanboYte.toRef(nguoivietbaocao)));
            diagnosticReport.setResultsInterpreter(listOf(EmrCanboYte.toRef(nguoidanhgiaketqua)));            
            diagnosticReport.setIssued(ngaygiobaocao);
            diagnosticReport.setConclusion(ketluan);
        }
        
        return mapOf(
                "serviceRequest", serviceRequest,
                "procedure", procedure,
                "diagnosticReport", diagnosticReport
             );
    }
	
	abstract public Map<String, Object> toFhir(Encounter enc);
}
