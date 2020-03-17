package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.emr.utils.FhirUtil.*;

public class ChanDoanHinhAnh extends BaseModelDTO {
    @JsonIgnore public DotKhamBenh dotKham;
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
    
    public String getEncounterId() {
        return dotKham != null? dotKham.id : null;
    }
    
    public ChanDoanHinhAnh() {
        super();
    }
    
    public ChanDoanHinhAnh(Procedure procedure) {
        super(procedure);
        
        if(procedure == null) return;
        
        // Procedure
        this.dotKham = DotKhamBenh.fromReference(procedure.getEncounter());
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
            return ServiceFactory.getDiagnosticReportService()
                                .getById(procedure.getReportFirstRep().getReference());
        }
        return null;
    }
    
    private static ServiceRequest getServiceRequest(@Nonnull  Procedure procedure) {
        if(procedure.hasBasedOn()) {
            return ServiceFactory.getServiceRequestService()
                                .getById(procedure.getBasedOnFirstRep().getReference());
                                                                   
        }
        return null;
    }
    
    public static ChanDoanHinhAnh fromFhir(Procedure procedure) {        
        if(procedure == null) return null;
        return new ChanDoanHinhAnh(procedure);
    }
    
    public static Map<String, Resource> toFhir(ChanDoanHinhAnh cdha) {
        if(cdha == null || cdha.dotKham == null) return null;
        cdha.dotKham = DotKhamBenh.fromFhirId(cdha.dotKham.id);
        
        if(cdha.dotKham == null || cdha.dotKham.benhNhan == null) return null;
        
        // Procedure
        var procedure = ServiceFactory.getProcedureService().getById(cdha.id);
        if(procedure == null) {
            procedure = new Procedure();
            procedure.setId(StringUtil.generateUID());
        }
        
        procedure.setSubject(BaseModelDTO.toReference(cdha.dotKham.benhNhan));
        procedure.setEncounter(BaseModelDTO.toReference(cdha.dotKham));
        procedure.setAsserter(BaseModelDTO.toReference(cdha.bacSiChuyenKhoa));
        if(cdha.ngayThucHien != null) procedure.setPerformed(new DateTimeType(cdha.ngayThucHien));
        procedure.setCode(DanhMuc.toConcept(cdha.dmCdha, CodeSystemValue.CHAN_DOAN_HINH_ANH));
        procedure.setOutcome(createCodeableConcept(cdha.ketQua));
        procedure.setFollowUp(listOf(createCodeableConcept(cdha.loiDan)));
        
        //ServiceRequest
        var serviceRequest = getServiceRequest(procedure);
        if(serviceRequest == null) {
            serviceRequest = new ServiceRequest();
            serviceRequest.setId(StringUtil.generateUID());
            procedure.setBasedOn(listOf(new Reference(serviceRequest)));
        }
        
        serviceRequest.setSubject(BaseModelDTO.toReference(cdha.dotKham.benhNhan));
        serviceRequest.setEncounter(BaseModelDTO.toReference(cdha.dotKham));
        serviceRequest.setRequester(CanboYte.toReference(cdha.bacSiYeuCau));
        serviceRequest.setAuthoredOn(cdha.ngayYeuCau);
        serviceRequest.setCode(procedure.getCode());
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(cdha.noiDungYeuCau)));
        
        
        // DiagnosticReport
        var diagnosticReport = getDiagnosticReport(procedure);
        if(diagnosticReport == null) {
            diagnosticReport = new DiagnosticReport();
            diagnosticReport.setId(StringUtil.generateUID());
            procedure.setReport(listOf(createReference(diagnosticReport)));
        }
        
        diagnosticReport.setSubject(BaseModelDTO.toReference(cdha.dotKham.benhNhan));
        diagnosticReport.setEncounter(BaseModelDTO.toReference(cdha.dotKham));
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(cdha.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(cdha.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(cdha.ngayGioBaoCao);
        diagnosticReport.setCode(procedure.getCode());
        diagnosticReport.setConclusion(cdha.ketLuan);
                                
        
        return Map.of("procedure", procedure, "serviceRequest", serviceRequest, "diagnosticReport", diagnosticReport);        
    }    
}
