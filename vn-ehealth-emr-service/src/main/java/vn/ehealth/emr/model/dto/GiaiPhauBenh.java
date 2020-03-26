package vn.ehealth.emr.model.dto;

import java.util.Date;
import java.util.Map;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.StringType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.dto.controller.DichVuKyThuatHelper;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.ExtensionURL;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.emr.utils.MessageUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class GiaiPhauBenh extends DichVuKyThuat {
    public DanhMuc dmGpb;
    public DanhMuc dmViTriLayMau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayLayMau;
    
    public String maChanDoanGpb;
    public String moTaChanDoanGpb;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public BaseRef bacSiYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public BaseRef bacSiChuyenKhoa;
    
    public String nhanXetViThe;
    public String nhanXetDaiThe;
    
    public String ketLuan;
    
    public BaseRef nguoiVietBaoCao;
    public BaseRef nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao;    
    
    public GiaiPhauBenh() {
        super();
    }
    
    public GiaiPhauBenh(Procedure procedure, boolean includeSpecimen) {
        super(procedure, includeSpecimen,  false);
    }
   
    @Override
    public Map<String, Object> toFhir() {
    	var gpbConcept = createCodeableConcept(LoaiDichVuKT.GIAI_PHAU_BENH, 
                MessageUtils.get("text.XRC"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    	
    	var code = DanhMuc.toConcept(this.dmGpb, CodeSystemValue.DICH_VU_KY_THUAT);
    	var subject = BaseRef.toPatientRef(this.patient);
    	var encounter = BaseRef.toEncounterRef(this.encounter);
    	
    	//Procedure
    	var procedure = new Procedure();
    	procedure.setId(this.id);
    	procedure.setCategory(gpbConcept);
    	procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
    	
        //ServiceRequest
        var serviceRequest = new ServiceRequest();
        serviceRequest.setCategory(listOf(gpbConcept));
        serviceRequest.setCode(code);
        serviceRequest.setSubject(subject);
		serviceRequest.setEncounter(encounter);
        
        serviceRequest.setAuthoredOn(this.ngayYeuCau);
        serviceRequest.setRequester(BaseRef.toPractitionerRef(this.bacSiYeuCau));
               
        // Specimen
        var specimen = new Specimen();
        specimen.setSubject(subject); 
        specimen.setReceivedTime(this.ngayThucHien);
        specimen.getCollection().setCollector(BaseRef.toPractitionerRef(this.bacSiChuyenKhoa));
        
        specimen.getCollection().setBodySite(DanhMuc.toConcept(this.dmViTriLayMau, CodeSystemValue.VI_TRI_MAU_SINH_THIET));
        specimen.getCollection().setCollected(new DateTimeType(this.ngayLayMau));
        
        var nxDaiTheExt = createExtension(ExtensionURL.DAI_THE_GPB, this.nhanXetDaiThe);
        var nxViTheExt = createExtension(ExtensionURL.VI_THE_GPB, this.nhanXetViThe);
        specimen.setExtension(listOf(nxDaiTheExt, nxViTheExt));	
                    
        // DiagnosticReport
        var diagnosticReport = new DiagnosticReport();
        diagnosticReport.setCategory(listOf(gpbConcept));
        diagnosticReport.setCode(code);
        diagnosticReport.setSubject(subject);
        diagnosticReport.setEncounter(encounter);
        diagnosticReport.setPerformer(listOf(BaseRef.toPractitionerRef(this.nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(BaseRef.toPractitionerRef(this.nguoiDanhGiaKetQua)));
        
        diagnosticReport.setIssued(this.ngayGioBaoCao);
        var conclusionCode = createCodeableConcept(this.maChanDoanGpb, this.moTaChanDoanGpb, CodeSystemValue.ICD10);
        diagnosticReport.getConclusionCode().add(conclusionCode);
                
        diagnosticReport.setConclusion(this.ketLuan);
        
        return mapOf(
        			"procedure", procedure,
                    "serviceRequest", serviceRequest,
                    "specimen", specimen,
                    "diagnosticReport", diagnosticReport
                 );
    }

    @Override
    public void fromFhir(Procedure procedure, boolean includeSpecimen, boolean includeObservation) {
        if(procedure == null) return;
        
        // ServiceRequest
        if(procedure.hasBasedOn()) {
        	var serviceRequest = (ServiceRequest) procedure.getBasedOnFirstRep().getResource();
        	this.dmGpb = new DanhMuc(serviceRequest.getCode());
        	if(serviceRequest != null) {
        		this.ngayYeuCau = serviceRequest.getAuthoredOn();                
                this.bacSiYeuCau = BaseRef.fromPractitionerRef(serviceRequest.getRequester());
        	}
        	
        	// Specimen
        	if(includeSpecimen) {
        		var specimen = DichVuKyThuatHelper.getSpecimen(serviceRequest);
        		
        		if(specimen != null) {
                    this.ngayThucHien =  specimen.getReceivedTime();
                    this.bacSiChuyenKhoa = BaseRef.fromPractitionerRef(specimen.getCollection().getCollector());
                    
                    this.dmViTriLayMau =  new DanhMuc(specimen.getCollection().getBodySite());
                    this.ngayLayMau = specimen.getCollection().hasCollectedDateTimeType()?specimen.getCollection().getCollectedDateTimeType().getValue() : null;
                    
                    if(specimen.hasExtension()) {
                        var extDaiThe = findExtensionByURL(specimen.getExtension(), ExtensionURL.DAI_THE_GPB);
                        if(extDaiThe != null && extDaiThe.getValue() instanceof StringType) {
                            this.nhanXetDaiThe = ((StringType) extDaiThe.getValue()).getValue();
                        }
                        var extViThe = findExtensionByURL(specimen.getExtension(), ExtensionURL.VI_THE_GPB);
                        if(extViThe != null && extViThe.getValue() instanceof StringType) {
                            this.nhanXetViThe = ((StringType) extViThe.getValue()).getValue();
                        }
                    }
                }
        	}
        }
        
        // DiagnosticReport
        if(procedure.hasReport()) {
        	var diagnosticReport = (DiagnosticReport) procedure.getReportFirstRep().getResource();
        	if(diagnosticReport != null) {
                if(diagnosticReport.hasPerformer()) {
                	this.nguoiVietBaoCao = new BaseRef(diagnosticReport.getPerformerFirstRep());
                }
                
                this.ngayGioBaoCao = diagnosticReport.getIssued();
                
                if(diagnosticReport.hasResultsInterpreter()) {
                	this.nguoiDanhGiaKetQua = new BaseRef(diagnosticReport.getResultsInterpreterFirstRep());
                }

                if(diagnosticReport.hasConclusionCode()) {
                    var conclusionCode = diagnosticReport.getConclusionCodeFirstRep();
                    if(conclusionCode.hasCoding()) {
                        var coding = conclusionCode.getCodingFirstRep();
                        this.maChanDoanGpb = coding.getCode();
                        this.moTaChanDoanGpb = coding.getDisplay();
                    }
                }
                
                this.ketLuan = diagnosticReport.getConclusion();
            }
        }
          
    }
}
