package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.dto.controller.DichVuKyThuatHelper;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class XetNghiem extends DichVuKyThuat {

    public static class KetQuaXetNghiem {
        public String giaTri;
        public DanhMuc dmChiSoXetNghiem;
    }
    
    public DanhMuc dmXetNghiem;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public BaseRef bacSiYeuCau;
    public String noiDungYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public BaseRef bacSiXetNghiem;
    public String nhanXet;
    
    public List<KetQuaXetNghiem> dsKetQuaXetNghiem;
    
    public BaseRef nguoiVietBaoCao;
    public BaseRef nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;
    
    public XetNghiem() {
        super();
    }
    
    public XetNghiem(Procedure procedure, boolean includeObservation) {
        super(procedure, false, includeObservation);
    }

    @Override
    public Map<String, Object> toFhir() {
    	var cdhaConcept = createCodeableConcept(LoaiDichVuKT.XET_NGHIEM, 
                MessageUtils.get("text.LAB"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    	
    	var code = DanhMuc.toConcept(this.dmXetNghiem, CodeSystemValue.DICH_VU_KY_THUAT);
    	var subject = BaseRef.toPatientRef(this.patient);
    	var encounter = BaseRef.toEncounterRef(this.encounter);
    	
    	var procedure = new Procedure();
    	procedure.setId(this.id);
    	procedure.setCategory(cdhaConcept);
    	procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
        procedure.setAsserter(BaseRef.toPractitionerRef(this.bacSiXetNghiem));
        
        if(this.ngayThucHien != null) {
        	procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        }
         
        //ServiceRequest
        var serviceRequest = new ServiceRequest();
        serviceRequest.setCategory(listOf(cdhaConcept));
        serviceRequest.setCode(code);
		serviceRequest.setSubject(subject);
		serviceRequest.setEncounter(encounter);
		serviceRequest.setRequester(BaseRef.toPractitionerRef(this.bacSiYeuCau));
		
		serviceRequest.setAuthoredOn(this.ngayYeuCau);        
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(this.noiDungYeuCau)));
        
        // Observations
        var observations = new ArrayList<Observation>();
        if(this.dsKetQuaXetNghiem != null) {
            for(var ketQuaXetNghiem : dsKetQuaXetNghiem) {
                var obs = new Observation();
                obs.setSubject(serviceRequest.getSubject());
                obs.setEncounter(serviceRequest.getEncounter());
                
                obs.setCode(DanhMuc.toConcept(ketQuaXetNghiem.dmChiSoXetNghiem, CodeSystemValue.CHI_SO_XET_NGHIEM));
                obs.setValue(new StringType(ketQuaXetNghiem.giaTri));
                observations.add(obs);
            }
        }        
                    
        
        // DiagnosticReport
        var diagnosticReport = new DiagnosticReport();
        diagnosticReport.setCategory(listOf(cdhaConcept));
        diagnosticReport.setCode(serviceRequest.getCode());
        diagnosticReport.setSubject(subject);
        diagnosticReport.setEncounter(encounter);
        diagnosticReport.setPerformer(listOf(BaseRef.toPractitionerRef(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseRef.toPractitionerRef(this.nguoiDanhGiaKetQua)));
        diagnosticReport.setIssued(this.ngayGioBaoCao);                
        
        return mapOf(
                    "serviceRequest", serviceRequest,
                    "procedure", procedure,
                    "observations", observations,
                    "diagnosticReport", diagnosticReport
                 );
    }

    @Override
    protected void fromFhir(Procedure procedure, boolean includeSpecimen, boolean includeObservation) {
    	if(procedure == null) return;
        
        // Procedure        
        this.dmXetNghiem = new DanhMuc(procedure.getCode());
        this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
        this.bacSiXetNghiem = BaseRef.fromPatientRef(procedure.getAsserter());
        
        // ServiceRequest
        if(procedure.hasBasedOn()) {
        	var serviceRequest = (ServiceRequest) procedure.getBasedOnFirstRep().getResource();
        	if(serviceRequest != null) {
        		this.ngayYeuCau = serviceRequest.getAuthoredOn();
                this.noiDungYeuCau = serviceRequest.hasOrderDetail()? serviceRequest.getOrderDetailFirstRep().getText() : "";
                this.bacSiYeuCau = BaseRef.fromPatientRef(serviceRequest.getRequester());
        	}
        	        	
        	// Observations
        	if(includeObservation) {
	            var observations =  DichVuKyThuatHelper.getObservations(serviceRequest);
	            this.dsKetQuaXetNghiem = new ArrayList<>();
	            for(var item : observations) {
	                var obs = (Observation) item;
	                var ketQuaXn = new KetQuaXetNghiem();
	                ketQuaXn.giaTri = obs.hasValue()? obs.getValueStringType().getValue() : "";
	                ketQuaXn.dmChiSoXetNghiem = DanhMuc.fromConcept(obs.getCode());
	                this.dsKetQuaXetNghiem.add(ketQuaXn);
	            }
        	}
        }
        
        // DiagnosticReport
        if(procedure.hasReport()) {
        	var diagnosticReport = (DiagnosticReport) procedure.getReportFirstRep().getResource();
        	if(diagnosticReport != null) {
                if(diagnosticReport.hasPerformer()) {
                	this.nguoiVietBaoCao = new BaseRef(diagnosticReport.getPerformerFirstRep());
                }
                
                this.ngayGioBaoCao = diagnosticReport.getIssued();
                
                if(diagnosticReport.hasResultsInterpreter()) {
                	this.nguoiDanhGiaKetQua = new BaseRef(diagnosticReport.getResultsInterpreterFirstRep());
                }
            }
        }
        
    }    
}
