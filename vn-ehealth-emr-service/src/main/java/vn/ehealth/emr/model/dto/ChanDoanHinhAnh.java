package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.clinical.entity.ProcedureEntity;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.diagnostic.entity.DiagnosticReportEntity;
import vn.ehealth.hl7.fhir.diagnostic.entity.ServiceRequestEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

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
        return dotKham != null? dotKham.fhirId : null;
    }
    
    public ChanDoanHinhAnh() {
        super();
    }
    
    public ChanDoanHinhAnh(ProcedureEntity procedure) {
        super(procedure);
        
        if(procedure == null) return;
        
        // Procedure
        this.dotKham = DotKhamBenh.fromReference(procedure.encounter);
        this.dmCdha = new DanhMuc(procedure.code);
        this.ngayThucHien = procedure.performedDate;
        this.bacSiChuyenKhoa = CanboYte.fromReference(procedure.asserter);
        this.ketQua = procedure.getOutcome();
        this.loiDan = procedure.getFollowUp();
        
        // ServiceRequest
        var serviceRequest = getServiceRequestEntity(procedure);
        if(serviceRequest != null) {
            this.ngayYeuCau = serviceRequest.authoredOn;
            this.bacSiYeuCau = CanboYte.fromReference(serviceRequest.requester);
            this.noiDungYeuCau = serviceRequest.getOrderDetail();
        }
        
        // DiagnosticReport
        var diagnosticReport = getDiagnosticReportEntity(procedure);
        if(diagnosticReport != null) {
            this.nguoiVietBaoCao = CanboYte.fromReference(diagnosticReport.getFirstPeformer());
            this.ngayGioBaoCao = diagnosticReport.issued;
            this.nguoiDanhGiaKetQua = CanboYte.fromReference(diagnosticReport.getFirstResultsInterpreter());
            this.ketLuan = diagnosticReport.conclusion;
        }
    }
    
    private static DiagnosticReportEntity getDiagnosticReportEntity(@Nonnull ProcedureEntity procedure) {
        if(procedure.report != null && procedure.report.size() > 0) {
            return ServiceFactory.getDiagnosticReportService()
                                .getByFhirId(procedure.report.get(0).reference)
                                .orElse(null);                                   
        }
        return null;
    }
    
    private static ServiceRequestEntity getServiceRequestEntity(@Nonnull  ProcedureEntity procedure) {
        if(procedure.basedOn != null && procedure.basedOn.size() > 0) {
            return ServiceFactory.getServiceRequestService()
                                .getByFhirId(procedure.basedOn.get(0).reference)
                                .orElse(null);                                   
        }
        return null;
    }
    
    public static ChanDoanHinhAnh fromEntity(ProcedureEntity procedure) {        
        if(procedure == null) return null;
        return new ChanDoanHinhAnh(procedure);
    }
    
    public static Map<String, BaseResource> toEntity(ChanDoanHinhAnh cdha) {
        if(cdha == null || cdha.dotKham == null) return null;
        cdha.dotKham = DotKhamBenh.fromFhirId(cdha.dotKham.fhirId);
        
        if(cdha.dotKham == null || cdha.dotKham.benhNhan == null) return null;
        
        // Procedure
        var procedure = ServiceFactory.getProcedureService().getByFhirId(cdha.fhirId).orElse(null);
        if(procedure == null) {
            procedure = new ProcedureEntity();
            procedure.fhirId = StringUtil.generateUID();
        }
        
        procedure.subject = BaseModelDTO.toReference(cdha.dotKham.benhNhan);
        procedure.encounter = BaseModelDTO.toReference(cdha.dotKham);
        procedure.asserter = BaseModelDTO.toReference(cdha.bacSiChuyenKhoa);
        procedure.performedDate = cdha.ngayThucHien;
        procedure.code = DanhMuc.toBaseCodeableConcept(cdha.dmCdha, CodeSystem.CHAN_DOAN_HINH_ANH);
        procedure.outcome = new BaseCodeableConcept(cdha.ketQua);
        procedure.followUp = listOf(new BaseCodeableConcept(cdha.loiDan));
        
        //ServiceRequest
        var serviceRequest = getServiceRequestEntity(procedure);
        if(serviceRequest == null) {
            serviceRequest = new ServiceRequestEntity();
            serviceRequest.fhirId = StringUtil.generateUID();
            procedure.basedOn = listOf(BaseReference.fromEntity(serviceRequest));
        }
        
        serviceRequest.subject = BaseModelDTO.toReference(cdha.dotKham.benhNhan);
        serviceRequest.encounter = BaseModelDTO.toReference(cdha.dotKham);
        serviceRequest.requester = CanboYte.toReference(cdha.bacSiYeuCau);
        serviceRequest.authoredOn = cdha.ngayYeuCau;
        serviceRequest.code = procedure.code;
        serviceRequest.orderDetail = listOf(new BaseCodeableConcept(cdha.noiDungYeuCau));
        
        
        // DiagnosticReport
        var diagnosticReport = getDiagnosticReportEntity(procedure);
        if(diagnosticReport == null) {
            diagnosticReport = new DiagnosticReportEntity();
            diagnosticReport.fhirId = StringUtil.generateUID();
            procedure.report = listOf(BaseReference.fromEntity(diagnosticReport));
        }
        
        diagnosticReport.subject = BaseModelDTO.toReference(cdha.dotKham.benhNhan);
        diagnosticReport.encounter = BaseModelDTO.toReference(cdha.dotKham);
        
        diagnosticReport.performer = listOf(BaseModelDTO.toReference(cdha.nguoiVietBaoCao));
        diagnosticReport.resultsInterpreter = listOf(BaseModelDTO.toReference(cdha.nguoiDanhGiaKetQua));
        
        diagnosticReport.issued = cdha.ngayGioBaoCao;
        diagnosticReport.code = procedure.code;
        diagnosticReport.conclusion = cdha.ketLuan;
                                
        
        return Map.of("procedure", procedure, "serviceRequest", serviceRequest, "diagnosticReport", diagnosticReport);        
    }    
}
