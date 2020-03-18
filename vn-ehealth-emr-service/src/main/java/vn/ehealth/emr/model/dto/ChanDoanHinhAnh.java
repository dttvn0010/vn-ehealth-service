package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

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
    
    public ChanDoanHinhAnh(DiagnosticReport diagnosticReport) {
        super(diagnosticReport);
        
        if(diagnosticReport == null) return;
        
        // DiagnosticReport
        this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
        this.ngayGioBaoCao = diagnosticReport.getIssued();
        this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
                                
        this.ketLuan = diagnosticReport.getConclusion();
        
        // Procedure
        var procedure = DaoFactory.getProcedureDao().getByReport(diagnosticReport.getIdElement());
        if(procedure != null) {
            this.encounterId = procedure.getEncounter().getReference();
            this.dmCdha = new DanhMuc(procedure.getCode());
            this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
            this.bacSiChuyenKhoa = CanboYte.fromReference(procedure.getAsserter());
            this.ketQua = procedure.hasOutcome()? procedure.getOutcome().getText() : "";
            this.loiDan = procedure.hasFollowUp()? procedure.getFollowUpFirstRep().getText() : "";
        }
        
        // ServiceRequest
        var serviceRequest = DaoFactory.getServiceRequestDao().getByReport(diagnosticReport.getIdElement());
        if(serviceRequest != null) {
            this.ngayYeuCau = serviceRequest.getAuthoredOn();
            this.bacSiYeuCau = CanboYte.fromReference(serviceRequest.getRequester());
            this.noiDungYeuCau = serviceRequest.hasOrderDetail()? serviceRequest.getOrderDetailFirstRep().getText() : "";
        }
    }
    
    public static ChanDoanHinhAnh fromFhir(DiagnosticReport diagnosticReport) {        
        if(diagnosticReport == null) return null;
        return new ChanDoanHinhAnh(diagnosticReport);
    }
    
    public static Map<String, Resource> toFhir(ChanDoanHinhAnh cdha) {
        if(cdha == null) return null;
        var dotKhamBenh = DotKhamBenh.fromFhirId(cdha.encounterId);
        if(dotKhamBenh == null || dotKhamBenh.benhNhan == null) return null;
        
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(cdha.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().read(cdha.getIdPart());
            if(diagnosticReport == null) throw new RuntimeException("Null diagnosticReport:" + cdha.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
        
        var cdhaConcept = createCodeableConcept(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                                            MessageUtils.get("text.CT"), 
                                            CodeSystemValue.DICH_VU_KY_THUAT);
                                            
        diagnosticReport.setCategory(listOf(cdhaConcept));
        diagnosticReport.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        diagnosticReport.setEncounter(new Reference(cdha.encounterId));
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(cdha.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(cdha.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(cdha.ngayGioBaoCao);
        diagnosticReport.setCode(DanhMuc.toConcept(cdha.dmCdha, CodeSystemValue.CHAN_DOAN_HINH_ANH));
        diagnosticReport.setConclusion(cdha.ketLuan);
        
        //ServiceRequest
        ServiceRequest serviceRequest;
        if(cdha.id != null) {
            serviceRequest = DaoFactory.getServiceRequestDao().getByReport(cdha.getIdPart());
            if(serviceRequest == null) throw new RuntimeException("No serviceRequest with reportId:" + cdha.id);
        }else {
            serviceRequest = new ServiceRequest();
        }
        
        serviceRequest.setSubject(diagnosticReport.getSubject());
        serviceRequest.setEncounter(diagnosticReport.getEncounter());
        serviceRequest.setRequester(CanboYte.toReference(cdha.bacSiYeuCau));
        serviceRequest.setAuthoredOn(cdha.ngayYeuCau);
        serviceRequest.setCode(diagnosticReport.getCode());
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(cdha.noiDungYeuCau)));
        
        // Procedure
        Procedure procedure;
        if(cdha.id != null) {
            procedure = DaoFactory.getProcedureDao().getByReport(cdha.getIdPart());
            if(procedure == null) throw new RuntimeException("No procedure with reportId:" + cdha.id);
        }else {
            procedure = new Procedure();
        }
        
        procedure.setSubject(diagnosticReport.getSubject());
        procedure.setEncounter(diagnosticReport.getEncounter());
        procedure.setAsserter(BaseModelDTO.toReference(cdha.bacSiChuyenKhoa));
        if(cdha.ngayThucHien != null) procedure.setPerformed(new DateTimeType(cdha.ngayThucHien));
        procedure.setCode(diagnosticReport.getCode());
        procedure.setOutcome(createCodeableConcept(cdha.ketQua));
        procedure.setFollowUp(listOf(createCodeableConcept(cdha.loiDan)));
                     
        return Map.of( 
                    "procedure", procedure, 
                    "serviceRequest", serviceRequest, 
                    "diagnosticReport", diagnosticReport
                 );
        
    }
}
