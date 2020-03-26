package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.emr.utils.MessageUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class ChanDoanHinhAnh extends DichVuKyThuat {
    public DanhMuc dmCdha;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public BaseRef bacSiYeuCau;
    public String noiDungYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public BaseRef bacSiChuyenKhoa;
    public String ketQua;
    public String ketLuan;
    public String loiDan;
    
    public BaseRef nguoiVietBaoCao;
    public BaseRef nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;    
    
    public ChanDoanHinhAnh() {
        super();
    }
    
    public ChanDoanHinhAnh(Procedure procedure) {
        super(procedure, false, false);
    }
    
    @Override
    public Map<String, Object> toFhir() {
    	var cdhaConcept = createCodeableConcept(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                MessageUtils.get("text.CT"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    	
    	var code = DanhMuc.toConcept(this.dmCdha, CodeSystemValue.DICH_VU_KY_THUAT);
    	var subject = BaseRef.toPatientRef(this.patient);
    	var encounter = BaseRef.toEncounterRef(this.encounter);
    	
    	var procedure = new Procedure();
    	procedure.setId(this.id);
    	procedure.setCategory(cdhaConcept);
    	procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
        procedure.setAsserter(BaseRef.toPatientRef(this.bacSiChuyenKhoa));
        
        if(this.ngayThucHien != null) {
        	procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        }
                
        procedure.setOutcome(createCodeableConcept(this.ketQua));
        procedure.setFollowUp(listOf(createCodeableConcept(this.loiDan)));
    	
        //ServiceRequest
        var serviceRequest = new ServiceRequest();
        serviceRequest.setCategory(listOf(cdhaConcept));
        serviceRequest.setCode(code);
		serviceRequest.setSubject(subject);
		serviceRequest.setEncounter(encounter);
		serviceRequest.setRequester(BaseRef.toPractitionerRef(this.bacSiYeuCau));
		
		serviceRequest.setAuthoredOn(this.ngayYeuCau);        
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(this.noiDungYeuCau)));
                                    
        // DiagnosticReport
        var diagnosticReport = new DiagnosticReport();
        diagnosticReport.setCategory(listOf(cdhaConcept));
        diagnosticReport.setCode(serviceRequest.getCode());
        diagnosticReport.setSubject(subject);
        diagnosticReport.setEncounter(encounter);
        diagnosticReport.setPerformer(listOf(BaseRef.toPractitionerRef(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseRef.toPractitionerRef(this.nguoiDanhGiaKetQua)));        
        diagnosticReport.setIssued(this.ngayGioBaoCao);                
        diagnosticReport.setConclusion(this.ketLuan);
        
        return mapOf(
                    "serviceRequest", serviceRequest,
                    "procedure", procedure,
                    "diagnosticReport", diagnosticReport
                 );
    }

    @Override
    public void fromFhir(Procedure procedure, boolean includeSpecimen, boolean includeObservation) {
        if(procedure == null) return;
        
        // Procedure        
        this.dmCdha = new DanhMuc(procedure.getCode());
        this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
        this.bacSiChuyenKhoa = BaseRef.fromPractitionerRef(procedure.getAsserter());
        
        this.ketQua = procedure.hasOutcome()? procedure.getOutcome().getText() : "";
        this.loiDan = procedure.hasFollowUp()? procedure.getFollowUpFirstRep().getText() : "";
        
        // ServiceRequest
        if(procedure.hasBasedOn()) {
        	var serviceRequest = (ServiceRequest) procedure.getBasedOnFirstRep().getResource();
        	if(serviceRequest != null) {
        		this.ngayYeuCau = serviceRequest.getAuthoredOn();
                this.noiDungYeuCau = serviceRequest.hasOrderDetail()? serviceRequest.getOrderDetailFirstRep().getText() : "";                
                this.bacSiYeuCau = BaseRef.fromPractitionerRef(serviceRequest.getRequester());
        	}
        }
        
        // DiagnosticReport
        if(procedure.hasReport()) {
        	var diagnosticReport = (DiagnosticReport) procedure.getReportFirstRep().getResource();
        	if(diagnosticReport != null) {
                if(diagnosticReport.hasPerformer()) {
                	this.nguoiVietBaoCao = BaseRef.fromPractitionerRef(diagnosticReport.getPerformerFirstRep());
                }
                
                this.ngayGioBaoCao = diagnosticReport.getIssued();
                
                if(diagnosticReport.hasResultsInterpreter()) {
                	this.nguoiDanhGiaKetQua = BaseRef.fromPractitionerRef(diagnosticReport.getResultsInterpreterFirstRep());
                }

                this.ketLuan = diagnosticReport.getConclusion();
            }
        }
    }
}
