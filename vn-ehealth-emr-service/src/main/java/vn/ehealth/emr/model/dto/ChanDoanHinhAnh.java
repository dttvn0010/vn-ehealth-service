package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class ChanDoanHinhAnh extends DichVuKyThuat {
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
    
    public ChanDoanHinhAnh(ServiceRequest serviceRequest) {
        super(serviceRequest);
    }
    
    @Override
    public Map<String, Object> toFhir() {
        var dotKhamBenh = DotKhamBenh.fromFhirId(this.encounterId);
        if(dotKhamBenh == null || dotKhamBenh.benhNhan == null) return null;
        
        //ServiceRequest
        ServiceRequest serviceRequest;
        if(this.id != null) {
            serviceRequest = DaoFactory.getServiceRequestDao().read(this.getIdPart());
            if(serviceRequest == null) throw new RuntimeException("No serviceRequest with id:" + this.id);
        }else {
            serviceRequest = new ServiceRequest();
        }
        
        var cdhaConcept = createCodeableConcept(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                MessageUtils.get("text.CT"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        
        serviceRequest.setCategory(listOf(cdhaConcept));
        serviceRequest.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        serviceRequest.setEncounter(createReference(ResourceType.Encounter, this.encounterId));
        serviceRequest.setRequester(CanboYte.toReference(this.bacSiYeuCau));
        serviceRequest.setAuthoredOn(this.ngayYeuCau);
        serviceRequest.setCode(DanhMuc.toConcept(this.dmCdha, CodeSystemValue.DICH_VU_KY_THUAT));
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(this.noiDungYeuCau)));
        
        // Procedure
        Procedure procedure;
        if(this.id != null) {
            procedure = DaoFactory.getProcedureDao().getByRequest(serviceRequest.getIdElement());
            if(procedure == null) throw new RuntimeException("No procedure with requestId:" + this.id);
        }else {
            procedure = new Procedure();
        }
        
        procedure.setCategory(cdhaConcept);
        procedure.setSubject(serviceRequest.getSubject());        
        procedure.setEncounter(serviceRequest.getEncounter());
        procedure.setAsserter(BaseModelDTO.toReference(this.bacSiChuyenKhoa));
        
        if(this.ngayThucHien != null) procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        
        procedure.setCode(serviceRequest.getCode());        
        procedure.setOutcome(createCodeableConcept(this.ketQua));
        procedure.setFollowUp(listOf(createCodeableConcept(this.loiDan)));
                    
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(this.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
            if(diagnosticReport == null) throw new RuntimeException("No diagnosticReport with requestId:" + this.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
                                                    
        diagnosticReport.setCategory(listOf(cdhaConcept));
        diagnosticReport.setSubject(serviceRequest.getSubject());
        diagnosticReport.setEncounter(serviceRequest.getEncounter());
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(this.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(this.ngayGioBaoCao);
        diagnosticReport.setCode(serviceRequest.getCode());        
        diagnosticReport.setConclusion(this.ketLuan);
        
        return mapOf(
                    entry("serviceRequest", serviceRequest),
                    entry("procedure", procedure),
                    entry("diagnosticReport", diagnosticReport)
                 );
    }

    @Override
    public void fromFhir(ServiceRequest serviceRequest) {
        if(serviceRequest == null) return;
        
        // ServiceRequest        
        this.encounterId = idFromRef(serviceRequest.getEncounter());
        this.ngayYeuCau = serviceRequest.getAuthoredOn();
        this.bacSiYeuCau = CanboYte.fromReference(serviceRequest.getRequester());
        this.noiDungYeuCau = serviceRequest.hasOrderDetail()? serviceRequest.getOrderDetailFirstRep().getText() : "";
        
        // Procedure
        var procedure = DaoFactory.getProcedureDao().getByRequest(serviceRequest.getIdElement());
        if(procedure != null) {
            this.ngayThucHien = procedure.hasPerformedDateTimeType()? procedure.getPerformedDateTimeType().getValue() : null;
            this.bacSiChuyenKhoa = CanboYte.fromReference(procedure.getAsserter());
            this.ketQua = procedure.hasOutcome()? procedure.getOutcome().getText() : "";
            this.loiDan = procedure.hasFollowUp()? procedure.getFollowUpFirstRep().getText() : "";
        }
        
        // DiagnosticReport
        var diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
        if(diagnosticReport != null) {
            this.dmCdha = new DanhMuc(diagnosticReport.getCode());
            this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                    CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
            this.ngayGioBaoCao = diagnosticReport.getIssued();
            this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                    CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
                                    
            this.ketLuan = diagnosticReport.getConclusion();
        }
        
    }
}
