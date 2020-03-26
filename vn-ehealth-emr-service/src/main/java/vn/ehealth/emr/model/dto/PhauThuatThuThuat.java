package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.ExtensionURL;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class PhauThuatThuThuat extends DichVuKyThuat {

    public static class ThanhVienPttt {
        public BaseRef bacsi;
        public DanhMuc dmVaiTro;
      
        public static ThanhVienPttt fromPerformer(ProcedurePerformerComponent performer) {
            if(performer == null) return null;
            var dto = new ThanhVienPttt();
            dto.dmVaiTro = DanhMuc.fromConcept(performer.getFunction());
            dto.bacsi = BaseRef.fromPractitionerRef(performer.getActor());
            return dto;
        }
        
        public static ProcedurePerformerComponent toPerformer(ThanhVienPttt dto) {
            if(dto == null) return null;
            var performer = new ProcedurePerformerComponent();
            performer.setFunction(DanhMuc.toConcept(dto.dmVaiTro, CodeSystemValue.VAI_TRO_PTTT));
            performer.setActor(BaseRef.toPractitionerRef(dto.bacsi));
            return performer;
        }
    }
    
    public PhauThuatThuThuat() {
        super();
    }
    
    public PhauThuatThuThuat(Procedure procedure) {
        super(procedure, false, false);
    }
    
    public DanhMuc dmPttt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public BaseRef bacSiYeuCau;
    public String noiDungYeuCau;
        
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    //public String chiDinhPttt;
    public String ketLuan;
    public String trinhTuPttt;
    public String ghiChu;
    
    public BaseRef thuKyGhiChep;
    public BaseRef chuTichHoiDong;
    public List<ThanhVienPttt> hoiDongPttt;    
        
    public BaseRef nguoiVietBaoCao;
    public BaseRef nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;
    
    @Override
    public Map<String, Object> toFhir() {
    	var cdhaConcept = createCodeableConcept(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                MessageUtils.get("text.SUR"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    	
    	var code = DanhMuc.toConcept(this.dmPttt, CodeSystemValue.DICH_VU_KY_THUAT);
    	var subject = BaseRef.toPatientRef(this.patient);
    	var encounter = BaseRef.toEncounterRef(this.encounter);
    	
    	// Procedure
    	var procedure = new Procedure();
    	procedure.setId(this.id);
    	procedure.setCategory(cdhaConcept);
    	procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
        
        procedure.setAsserter(BaseRef.toPractitionerRef(this.chuTichHoiDong));
        procedure.setRecorder(BaseRef.toPractitionerRef(this.thuKyGhiChep));
        
        if(this.ngayThucHien != null) {
        	procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        }
                
        procedure.setNote(listOf(createAnnotation(this.ghiChu)));
        procedure.setExtension(listOf(createExtension(ExtensionURL.TRINH_TU_PTTT, this.trinhTuPttt)));
        procedure.setPerformer(transform(this.hoiDongPttt, x -> ThanhVienPttt.toPerformer(x)));
        
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
        this.dmPttt = new DanhMuc(procedure.getCode());
        this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
        this.chuTichHoiDong = BaseRef.fromPractitionerRef(procedure.getAsserter());        
        this.thuKyGhiChep = BaseRef.fromPractitionerRef(procedure.getRecorder());
        
        if(procedure.hasPerformer()) {
            this.hoiDongPttt = transform(procedure.getPerformer(), x -> ThanhVienPttt.fromPerformer(x));
        }
        
        if(procedure.hasNote()) {
            this.ghiChu = procedure.getNoteFirstRep().getText();
        }

        if(procedure.hasExtension()) {
            var ext = findExtensionByURL(procedure.getExtension(), ExtensionURL.TRINH_TU_PTTT);
            if(ext != null && ext.getValue() instanceof StringType) {
                this.trinhTuPttt = ((StringType) ext.getValue()).getValue();
            }
        }
        
        if(procedure.hasPerformedDateTimeType()) {
        	this.ngayThucHien = procedure.getPerformedDateTimeType().getValue();
        }
        
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
