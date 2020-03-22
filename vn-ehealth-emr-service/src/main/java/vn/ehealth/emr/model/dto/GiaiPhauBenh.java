package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.StringType;

import com.fasterxml.jackson.annotation.JsonFormat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.ExtensionURL;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class GiaiPhauBenh extends DichVuKyThuat {
    public String encounterId;
    
    public DanhMuc dmGpb;
    public DanhMuc dmViTriLayMau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayLayMau;
    
    public String maChanDoanGpb;
    public String moTaChanDoanGpb;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public CanboYte bacSiChuyenKhoa;
    
    public String nhanXetViThe;
    public String nhanXetDaiThe;
    
    public String ketLuan;
    
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;    
    
    public GiaiPhauBenh() {
        super();
    }
    
    public GiaiPhauBenh(ServiceRequest serviceRequest) {
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
        
        var gpbConcept = createCodeableConcept(LoaiDichVuKT.GIAI_PHAU_BENH, 
                MessageUtils.get("text.SP"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        
        serviceRequest.setCategory(listOf(gpbConcept));
        serviceRequest.setCode(DanhMuc.toConcept(this.dmGpb, CodeSystemValue.DICH_VU_KY_THUAT));
        serviceRequest.setSubject(BaseModelDTO.toReference(dotKhamBenh.benhNhan));
        serviceRequest.setEncounter(createReference(ResourceType.Encounter, this.encounterId));
        serviceRequest.setRequester(CanboYte.toReference(this.bacSiYeuCau));
        serviceRequest.setAuthoredOn(this.ngayYeuCau);
       
        
        // Specimen
        Specimen specimen;
        if(this.id != null) {
        	specimen = DaoFactory.getSpecimenDao().getByRequest(serviceRequest.getIdElement());
            if(specimen == null) throw new RuntimeException("No specimen with requestId:" + this.id);
        }else {
        	specimen = new Specimen();
        }
        
        specimen.setSubject(serviceRequest.getSubject()); 
        specimen.setReceivedTime(this.ngayThucHien);
        
        specimen.getCollection().setCollector(CanboYte.toReference(this.bacSiChuyenKhoa));
        specimen.getCollection().setBodySite(DanhMuc.toConcept(this.dmViTriLayMau, CodeSystemValue.VI_TRI_MAU_SINH_THIET));
        specimen.getCollection().setCollected(new DateTimeType(this.ngayLayMau));
        
        specimen.setExtension(listOf(createExtension(ExtensionURL.DAI_THE_GPB, this.nhanXetDaiThe), createExtension(ExtensionURL.VI_THE_GPB, this.nhanXetViThe)));	
                    
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(this.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
            if(diagnosticReport == null) throw new RuntimeException("No diagnosticReport with requestId:" + this.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
                                                    
    
        diagnosticReport.setSubject(serviceRequest.getSubject());
        diagnosticReport.setEncounter(serviceRequest.getEncounter());
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(this.nguoiDanhGiaKetQua)));
        diagnosticReport.setIssued(this.ngayGioBaoCao);
        diagnosticReport.setCategory(listOf(gpbConcept));
        diagnosticReport.getConclusionCode().add(createCodeableConcept(this.maChanDoanGpb, this.moTaChanDoanGpb, null));
        
//        diagnosticReport.setCode(serviceRequest.getCode());        
        diagnosticReport.setConclusion(this.ketLuan);
        
        return mapOf(
                    entry("serviceRequest", serviceRequest),
                    entry("specimen", specimen),
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
        this.dmGpb = new DanhMuc(serviceRequest.getCode());
       
        // Specimen
        var specimen = DaoFactory.getSpecimenDao().getByRequest(serviceRequest.getIdElement());
        if(specimen != null) {
            this.ngayThucHien =  specimen.getReceivedTime();
            this.bacSiChuyenKhoa = CanboYte.fromReference(specimen.getCollection().getCollector());
            this.dmViTriLayMau =  new DanhMuc(specimen.getCollection().getBodySite());
            this.ngayLayMau = specimen.getCollection().hasCollectedDateTimeType()?specimen.getCollection().getCollectedDateTimeType().getValue() : null;
            
            if(specimen.hasExtension()) {
                var extDaiThe = findExtensionByURL(specimen.getExtension(), ExtensionURL.DAI_THE_GPB);
                if(extDaiThe != null && extDaiThe.getValue() instanceof StringType) {
                    this.nhanXetDaiThe = ((StringType) extDaiThe.getValue()).getValue();
                }
                var extViThe = findExtensionByURL(specimen.getExtension(), ExtensionURL.VI_THE_GPB);
                if(extViThe != null && extViThe.getValue() instanceof StringType) {
                    this.nhanXetViThe = ((StringType) extDaiThe.getValue()).getValue();
                }
            }
        }
        
        // DiagnosticReport
        var diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
        if(diagnosticReport != null) {
          
            this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                    CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
            this.ngayGioBaoCao = diagnosticReport.getIssued();
            this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                    CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
            this.maChanDoanGpb = diagnosticReport.hasConclusionCode() ? diagnosticReport.getConclusionCodeFirstRep().getCodingFirstRep().getCode() : null;
            this.moTaChanDoanGpb = diagnosticReport.hasConclusionCode() ? diagnosticReport.getConclusionCodeFirstRep().getText() : null;                           
            this.ketLuan = diagnosticReport.getConclusion();
        }
        
    }
}
