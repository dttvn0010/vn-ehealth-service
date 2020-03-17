package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import vn.ehealth.emr.utils.DbUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.emr.utils.FhirUtil.*;

public class ChanDoanHinhAnh extends BaseModelDTO {
    public String encounterId;
    
    public DanhMuc dmCdha;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public CanboYte bacSiChuyenKhoa;
    public String ketQua;
    public String ketLuan;
    public String loiDan;
    
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;    
     
    public ChanDoanHinhAnh() {
        super();
    }
    
    public ChanDoanHinhAnh(Procedure procedure) {
        super(procedure);
        
        if(procedure == null) return;
        
        // Procedure
        this.encounterId = procedure.getEncounter().getReference();
        this.dmCdha = new DanhMuc(procedure.getCode());
        this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
        this.bacSiChuyenKhoa = CanboYte.fromReference(procedure.getAsserter());
        this.ketQua = procedure.hasOutcome()? procedure.getOutcome().getText() : "";
        this.loiDan = procedure.hasFollowUp()? procedure.getFollowUpFirstRep().getText() : "";
        
        // ServiceRequest
        var serviceRequest = getServiceRequest(procedure);
        if(serviceRequest != null) {
            this.ngayYeuCau = serviceRequest.getAuthoredOn();
            this.bacSiYeuCau = CanboYte.fromReference(serviceRequest.getRequester());
            this.noiDungYeuCau = serviceRequest.hasOrderDetail()? serviceRequest.getOrderDetailFirstRep().getText() : "";
        }
        
        // DiagnosticReport
        var diagnosticReport = getDiagnosticReport(procedure);
        if(diagnosticReport != null) {
            this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                    CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
            this.ngayGioBaoCao = diagnosticReport.getIssued();
            this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                    CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
                                    
            this.ketLuan = diagnosticReport.getConclusion();
        }
    }
    
    private static DiagnosticReport getDiagnosticReport(@Nonnull Procedure procedure) {
        if(procedure.hasReport()) {
            String diagnosticReportId = procedure.getReportFirstRep().getReference();
            return DbUtils.getDiagnosticReportDao().read(new IdType(diagnosticReportId));
        }
        return null;
    }
    
    private static ServiceRequest getServiceRequest(@Nonnull  Procedure procedure) {
        if(procedure.hasBasedOn()) {
            var serviceRequestId = procedure.getBasedOnFirstRep().getReference();
            return DbUtils.getServiceRequestDao().read(new IdType(serviceRequestId));                                                                   
        }
        return null;
    }
    
    public static ChanDoanHinhAnh fromFhir(Procedure procedure) {        
        if(procedure == null) return null;
        return new ChanDoanHinhAnh(procedure);
    }
    
    public static Map<String, Resource> toFhir(ChanDoanHinhAnh cdha) {
        if(cdha == null) return null;
        var dotKhamBenh = DotKhamBenh.fromFhirId(cdha.encounterId);
        if(dotKhamBenh == null || dotKhamBenh.benhNhan == null) return null;
        
        // Procedure
        var procedure = DbUtils.getProcedureDao().read(new IdType(cdha.id));
        if(procedure == null) {
            procedure = new Procedure();
        }
        
        procedure.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        procedure.setEncounter(new Reference(cdha.encounterId));
        procedure.setAsserter(BaseModelDTO.toReference(cdha.bacSiChuyenKhoa));
        if(cdha.ngayThucHien != null) procedure.setPerformed(new DateTimeType(cdha.ngayThucHien));
        procedure.setCode(DanhMuc.toConcept(cdha.dmCdha, CodeSystemValue.CHAN_DOAN_HINH_ANH));
        procedure.setOutcome(createCodeableConcept(cdha.ketQua));
        procedure.setFollowUp(listOf(createCodeableConcept(cdha.loiDan)));
        
        //ServiceRequest
        var serviceRequest = new ServiceRequest();
        serviceRequest.setId(procedure.hasBasedOn()? procedure.getBasedOnFirstRep().getReference() : null);
        serviceRequest.setSubject(procedure.getSubject());
        serviceRequest.setEncounter(procedure.getEncounter());
        serviceRequest.setRequester(CanboYte.toReference(cdha.bacSiYeuCau));
        serviceRequest.setAuthoredOn(cdha.ngayYeuCau);
        serviceRequest.setCode(procedure.getCode());
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(cdha.noiDungYeuCau)));
                
        // DiagnosticReport
        var diagnosticReport = new DiagnosticReport();
        diagnosticReport.setId(procedure.hasReport()? procedure.getReportFirstRep().getReference() : null);
        diagnosticReport.setSubject(procedure.getSubject());
        diagnosticReport.setEncounter(procedure.getEncounter());
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(cdha.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(cdha.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(cdha.ngayGioBaoCao);
        diagnosticReport.setCode(procedure.getCode());
        diagnosticReport.setConclusion(cdha.ketLuan);
                     
        return Map.of( 
                    "procedure", procedure, 
                    "serviceRequest", serviceRequest, 
                    "diagnosticReport", diagnosticReport
                 );        
    }    
}
