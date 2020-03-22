package vn.ehealth.emr.model.dto;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.entry;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createCodeableConcept;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.idFromRef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

public class XetNghiem extends DichVuKyThuat {

    public static class KetQuaXetNghiem {
        public String giaTri;
        public DanhMuc dmChiSoXetNghiem;
    }
    
    public DanhMuc dmXetNghiem;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public CanboYte bacSiXetNghiem;
    public String nhanXet;
    
    public List<KetQuaXetNghiem> dsKetQuaXetNghiem;
    
    public CanboYte nguoiVietBaoCao;
    public CanboYte nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;
    
    public XetNghiem() {
        super();
    }
    
    public XetNghiem(ServiceRequest serviceRequest) {
        super(serviceRequest);
    }

    @Override
    public Map<String, Object> toFhir() {
        //ServiceRequest
        ServiceRequest serviceRequest;
        if(this.id != null) {
            serviceRequest = DaoFactory.getServiceRequestDao().read(this.getIdPart());
            if(serviceRequest == null) throw new RuntimeException("No serviceRequest with id:" + this.id);
        }else {
            serviceRequest = new ServiceRequest();
        }
        
        var xetnghiemConcept = createCodeableConcept(LoaiDichVuKT.XET_NGHIEM, 
                MessageUtils.get("text.LAB"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        
        serviceRequest.setCategory(listOf(xetnghiemConcept));
        serviceRequest.setSubject(createReference(ResourceType.Patient, this.patientId));
        serviceRequest.setEncounter(createReference(ResourceType.Encounter, this.encounterId));
        serviceRequest.setRequester(CanboYte.toReference(this.bacSiYeuCau));
        serviceRequest.setAuthoredOn(this.ngayYeuCau);
        serviceRequest.setCode(DanhMuc.toConcept(this.dmXetNghiem, CodeSystemValue.DICH_VU_KY_THUAT));
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(this.noiDungYeuCau)));
        
        // Procedure
        Procedure procedure;
        if(this.id != null) {
            procedure = DaoFactory.getProcedureDao().getByRequest(serviceRequest.getIdElement());
            if(procedure == null) throw new RuntimeException("No procedure with requestId:" + this.id);
        }else {
            procedure = new Procedure();
        }
        
        procedure.setCategory(xetnghiemConcept);
        procedure.setSubject(serviceRequest.getSubject());        
        procedure.setEncounter(serviceRequest.getEncounter());
        procedure.setAsserter(BaseModelDTO.toReference(this.bacSiXetNghiem));
        
        if(this.ngayThucHien != null) procedure.setPerformed(new DateTimeType(this.ngayThucHien));
        
        procedure.setCode(serviceRequest.getCode());
        
        // Observations
        var observations = new ArrayList<Observation>();
        if(this.dsKetQuaXetNghiem != null) {
            for(var ketQuaXetNghiem : dsKetQuaXetNghiem) {
                var obs = new Observation();
                obs.setSubject(serviceRequest.getSubject());
                obs.setEncounter(serviceRequest.getEncounter());
                
                obs.setCode(DanhMuc.toConcept(ketQuaXetNghiem.dmChiSoXetNghiem, CodeSystemValue.CHI_SO_XET_NGHIEM));
                obs.setValue(new StringType(ketQuaXetNghiem.giaTri));
                observations.add(obs);
            }
        }        
                    
        // DiagnosticReport
        DiagnosticReport diagnosticReport = null;
        if(this.id != null) {
            diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
            if(diagnosticReport == null) throw new RuntimeException("No diagnosticReport with requestId:" + this.id);
        }else {
            diagnosticReport = new DiagnosticReport();
        }
                                                    
        diagnosticReport.setCategory(listOf(xetnghiemConcept));
        diagnosticReport.setSubject(serviceRequest.getSubject());
        diagnosticReport.setEncounter(serviceRequest.getEncounter());
        
        diagnosticReport.setPerformer(listOf(BaseModelDTO.toReference(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseModelDTO.toReference(this.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(this.ngayGioBaoCao);
        diagnosticReport.setCode(serviceRequest.getCode());        
        
        return mapOf(
                    entry("serviceRequest", serviceRequest),
                    entry("procedure", procedure),
                    entry("observations", observations),
                    entry("diagnosticReport", diagnosticReport)
                 );
    }

    @Override
    protected void fromFhir(ServiceRequest serviceRequest) {
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
            this.bacSiXetNghiem = CanboYte.fromReference(procedure.getAsserter());
        }
        
        // Observations
        var observations =  DaoFactory.getObservationDao().getByRequest(serviceRequest.getIdElement());
        this.dsKetQuaXetNghiem = new ArrayList<>();
        for(var obs : observations) {
            var ketQuaXn = new KetQuaXetNghiem();
            ketQuaXn.giaTri = obs.hasValue()? obs.getValueStringType().getValue() : "";
            ketQuaXn.dmChiSoXetNghiem = DanhMuc.fromConcept(obs.getCode());
            this.dsKetQuaXetNghiem.add(ketQuaXn);
        }
                
        // DiagnosticReport
        var diagnosticReport = DaoFactory.getDiagnosticReportDao().getByRequest(serviceRequest.getIdElement());
        if(diagnosticReport != null) {
            this.dmXetNghiem = new DanhMuc(diagnosticReport.getCode());
            this.nguoiVietBaoCao = diagnosticReport.hasPerformer()?
                                    CanboYte.fromReference(diagnosticReport.getPerformerFirstRep()) : null;
            this.ngayGioBaoCao = diagnosticReport.getIssued();
            this.nguoiDanhGiaKetQua = diagnosticReport.hasResultsInterpreter()?
                                    CanboYte.fromReference(diagnosticReport.getResultsInterpreterFirstRep()) : null;
                                    
        }
        
    }    
}
