package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.ExtensionURL;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class PhauThuatThuThuat extends DichVuKyThuat {

    public static class ThanhVienPttt {
        public CanboYte bacsi;
        public DanhMuc dmVaiTro;
      
        public static ThanhVienPttt fromPerformer(ProcedurePerformerComponent performer) {
            if(performer == null) return null;
            var dto = new ThanhVienPttt();
            dto.dmVaiTro = DanhMuc.fromConcept(performer.getFunction());
            dto.bacsi = CanboYte.fromReference(performer.getActor());
            return dto;
        }
        
        public static ProcedurePerformerComponent toPerformer(ThanhVienPttt dto) {
            if(dto == null) return null;
            var performer = new ProcedurePerformerComponent();
            performer.setFunction(DanhMuc.toConcept(dto.dmVaiTro, CodeSystemValue.VAI_TRO_PTT));
            performer.setActor(BaseModelDTO.toReference(dto.bacsi));
            return performer;
        }
    }
    
    public PhauThuatThuThuat() {
        super();
    }
    
    public PhauThuatThuThuat(ServiceRequest serviceRequest) {
        super(serviceRequest);
    }
    
    public String encounterId;
    public DanhMuc dmPttt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
        
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    //public String chiDinhPttt;
    public String ketLuan;
    public String trinhTuPttt;
    public String ghiChu;
    
    public CanboYte thuKyGhiChep;
    public CanboYte chuTichHoiDong;
    public List<ThanhVienPttt> hoiDongPttt;    
        
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;
    
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
        
        var ptttConcept = createCodeableConcept(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                                MessageUtils.get("text.SUR"), 
                                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        
        serviceRequest.setCategory(listOf(ptttConcept));
        serviceRequest.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        serviceRequest.setEncounter(createReference(ResourceType.Encounter, this.encounterId));
        serviceRequest.setRequester(CanboYte.toReference(this.bacSiYeuCau));
        serviceRequest.setAuthoredOn(this.ngayYeuCau);
        serviceRequest.setCode(DanhMuc.toConcept(this.dmPttt, CodeSystemValue.DICH_VU_KY_THUAT));
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(this.noiDungYeuCau)));
        
        // Procedure
        Procedure procedure;
        if(this.id != null) {
            procedure = DaoFactory.getProcedureDao().getByRequest(serviceRequest.getIdElement());
            if(procedure == null) throw new RuntimeException("No procedure with requestId:" + this.id);
        }else {
            procedure = new Procedure();
        }
        
        procedure.setCategory(ptttConcept);
        procedure.setSubject(serviceRequest.getSubject());
        procedure.setEncounter(serviceRequest.getEncounter());
        procedure.setAsserter(BaseModelDTO.toReference(this.chuTichHoiDong));
        procedure.setRecorder(BaseModelDTO.toReference(this.thuKyGhiChep));
        if(this.ngayThucHien != null) procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        procedure.setCode(serviceRequest.getCode());        
        procedure.setNote(listOf(createAnnotation(this.ghiChu)));
        procedure.setExtension(listOf(createExtension(ExtensionURL.TRINH_TU_PTTT, this.trinhTuPttt)));
        procedure.setPerformer(transform(this.hoiDongPttt, x -> ThanhVienPttt.toPerformer(x)));
        
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(this.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().read(this.getIdPart());
            if(diagnosticReport == null) throw new RuntimeException("Null diagnosticReport:" + this.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
        
        diagnosticReport.setCategory(listOf(ptttConcept));
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
            
            if(procedure.hasExtension()) {
                var ext = findExtensionByURL(procedure.getExtension(), ExtensionURL.TRINH_TU_PTTT);
                if(ext != null && ext.getValue() instanceof StringType) {
                    this.trinhTuPttt = ((StringType) ext.getValue()).getValue();
                }
            }
            
            this.chuTichHoiDong = CanboYte.fromReference(procedure.getAsserter());
            this.thuKyGhiChep = CanboYte.fromReference(procedure.getRecorder());
            
            if(procedure.hasPerformer()) {
                this.hoiDongPttt = transform(procedure.getPerformer(), x -> ThanhVienPttt.fromPerformer(x));
            }
            
            if(procedure.hasNote()) {
                this.ghiChu = procedure.getNoteFirstRep().getText();
            }
        }
        
        // DiagnosticReport
        var diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
        if(diagnosticReport != null) {
            this.dmPttt = new DanhMuc(diagnosticReport.getCode());        
            this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                    CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
            
            this.ngayGioBaoCao = diagnosticReport.getIssued();
         
            this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                    CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
            
            this.ketLuan = diagnosticReport.getConclusion();
        }
    }    
}
