package vn.ehealth.emr.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
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

public class PhauThuatThuThuat extends BaseModelDTO {

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
    
    public String encounterId;
    public DanhMuc dmPttt;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
        
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public String chiDinhPttt;
    public String ketLuan;
    public String trinhTuPttt;
    public String ghiChu;
    
    public CanboYte thuKyGhiChep;
    public CanboYte chuTichHoiDong;
    public List<ThanhVienPttt> hoiDongPttt = new ArrayList<>();    
        
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;
    
    public PhauThuatThuThuat() {
        super();
    }
    
    public PhauThuatThuThuat(DiagnosticReport diagnosticReport) {
        super(diagnosticReport);
        if(diagnosticReport == null) return;
        
        // DiagnosticReport
        this.encounterId = diagnosticReport.getEncounter().getReference();
        this.dmPttt = new DanhMuc(diagnosticReport.getCode());        
        this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
        
        this.ngayGioBaoCao = diagnosticReport.getIssued();
     
        this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
        
        if(diagnosticReport.hasBasedOn()) {                        
            this.chiDinhPttt = diagnosticReport.getBasedOnFirstRep().getReference();
        }
        
        this.ketLuan = diagnosticReport.getConclusion();
        
        // Procedure
        var procedure = DaoFactory.getProcedureDao().getByReport(diagnosticReport.getIdElement());
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
    }
    
    public static PhauThuatThuThuat fromFhir(DiagnosticReport diagnosticReport) {
        if(diagnosticReport == null) return null;
        return new PhauThuatThuThuat(diagnosticReport);
    }
    
    public static Map<String, Resource> toFhir(PhauThuatThuThuat pttt) {
        if(pttt == null) return null;
        
        var dotKhamBenh = DotKhamBenh.fromFhirId(pttt.encounterId);
        if(dotKhamBenh == null || dotKhamBenh.benhNhan == null) return null;
        
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(pttt.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().read(pttt.getIdPart());
            if(diagnosticReport == null) throw new RuntimeException("Null diagnosticReport:" + pttt.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
        
        var ptttConcept = createCodeableConcept(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                MessageUtils.get("text.SUR"), 
                CodeSystemValue.DICH_VU_KY_THUAT);
        
        diagnosticReport.setCategory(listOf(ptttConcept));
        diagnosticReport.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        diagnosticReport.setEncounter(new Reference(pttt.encounterId));
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(pttt.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(pttt.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(pttt.ngayGioBaoCao);
        diagnosticReport.setCode(DanhMuc.toConcept(pttt.dmPttt, CodeSystemValue.CHAN_DOAN_HINH_ANH));
        diagnosticReport.setConclusion(pttt.ketLuan);
        
        // Procedure
        Procedure procedure;
        if(pttt.id != null) {
            procedure = DaoFactory.getProcedureDao().getByReport(pttt.getIdPart());
            if(procedure == null) throw new RuntimeException("No procedure with reportId:" + pttt.id);
        }else {
            procedure = new Procedure();
        }
        
        procedure.setSubject(diagnosticReport.getSubject());
        procedure.setEncounter(diagnosticReport.getEncounter());
        procedure.setAsserter(BaseModelDTO.toReference(pttt.chuTichHoiDong));
        if(pttt.ngayThucHien != null) procedure.setPerformed(new DateTimeType(pttt.ngayThucHien));
        procedure.setCode(diagnosticReport.getCode());        
        procedure.setNote(listOf(createAnnotation(pttt.ghiChu)));
        procedure.setExtension(listOf(createExtension(ExtensionURL.TRINH_TU_PTTT, pttt.trinhTuPttt)));
        procedure.setPerformer(transform(pttt.hoiDongPttt, x -> ThanhVienPttt.toPerformer(x)));
        
        return mapOf(
                    entry("diagnosticReport", diagnosticReport), 
                    entry("procedure", procedure)
                );
    }

    @Override
    public ResourceType getType() {
        return ResourceType.DiagnosticReport;
    }
    
}
