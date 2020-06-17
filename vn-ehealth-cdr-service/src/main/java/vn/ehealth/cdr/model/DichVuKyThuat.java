package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.component.DanhMuc;
import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "dich_vu_ky_thuat")
public class DichVuKyThuat {
	
    public static class ThanhVienThucHien {
        public DanhMuc dmVaiTro;        
        public CanboYteDTO bacSi;
        
        public static ProcedurePerformerComponent toPerformer(ThanhVienThucHien dto) {
            if(dto == null) return null;
            var performer = new ProcedurePerformerComponent();
            performer.setFunction(DanhMuc.toConcept(dto.dmVaiTro, CodeSystemValue.VAI_TRO_PTTT));
            performer.setActor(CanboYteDTO.toRef(dto.bacSi));
            return performer;
        }
    }
    
    @Id public ObjectId id;        
    
    public EmrRef hoSoBenhAnRef;
    public EmrRef benhNhanRef;
    public EmrRef coSoKhamBenhRef;
    public EmrRef ylenhRef;
    
    public String idhis;
    
	public DanhMuc dmKhoaDieuTri;
	
	public DanhMuc dmLoaiDVKT;
	
	public DanhMuc dmDVKT;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYteDTO bacSiYeuCau;
    public String noiDungYeuCau;
    
    public List<DanhMuc> dsDmMaBenhChanDoanSau = new ArrayList<>();
    public String moTaChanDoanTruoc;
    
    public List<DanhMuc> dsDmMaBenhChanDoanTruoc = new ArrayList<>();    
    public String moTaChanDoanSau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    public CanboYteDTO bacSiThucHien;
    public DanhMuc dmNoiThucHien;
        
    public List<ThanhVienThucHien> dsThanhVien = new ArrayList<>();
    
    public CanboYteDTO thuKyGhiChep;
    public CanboYteDTO nguoiVietBaoCao;
    public CanboYteDTO nguoiDanhGiaKetQua;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioBaoCao; 
    
    public String ghiChu;
    public String loiDan;
    public String ketLuan;
    public String ketQua;
    public String nhanXet;
    
    public static class KetQuaXetNghiem {
        public DanhMuc dmDichKetQuaXetNghiem;
        
        public DanhMuc dmChiSoXetNghiem;

        public String giaTriDo;
    }
    
    public List<KetQuaXetNghiem> dsKetQuaXetNghiem = new ArrayList<>();
    
    public Map<String, Object> extra = new HashMap<>();
    public List<FileDinhKem> dsFileDinhKem = new ArrayList<>();
    
    public CodeableConcept getCategory() {
        return DanhMuc.toConcept(dmLoaiDVKT, CodeSystemValue.DIAGNOSTIC_SERVICE_SECTIONS);
    }
    
    public CodeableConcept getCode() {
        return DanhMuc.toConcept(dmDVKT, CodeSystemValue.DICH_VU_KY_THUAT);
    }
    
	public Map<String, Object> toFhir(Encounter enc) {
	   
        if(enc == null) new HashMap<>();
        
        var procedure = new Procedure();
        var serviceRequest = new ServiceRequest();
        var diagnosticReport = new DiagnosticReport();
        
        var code = getCode();
        var category = getCategory();
        var subject = enc.getSubject();
        var encounter = createReference(enc);
        
        // Procedure
        
        String loaiDVKT = "";
        if(category != null && category.hasCoding()) {
            loaiDVKT = category.getCodingFirstRep().getCode();
        }
        
        if(!StringUtils.isBlank(idhis) && !StringUtils.isBlank(loaiDVKT)) {
            var identifier = loaiDVKT + "/" + idhis;
            procedure.setIdentifier(listOf(createIdentifier(identifier, "")));
        }
        
        procedure.setCategory(category);
        procedure.setCode(code);
        procedure.setSubject(subject);        
        procedure.setEncounter(encounter);
        
        procedure.setAsserter(CanboYteDTO.toRef(bacSiThucHien));
        procedure.setRecorder(CanboYteDTO.toRef(thuKyGhiChep));
        
        if(ngayThucHien != null) {
            procedure.setPerformed(new DateTimeType(ngayThucHien));
        }
                
        procedure.setOutcome(createCodeableConcept(ketQua));
        procedure.setNote(listOf(createAnnotation(ghiChu)));
        procedure.setFollowUp(listOf(createCodeableConcept(loiDan)));
        procedure.setPerformer(transform(dsThanhVien, x -> ThanhVienThucHien.toPerformer(x)));
        
        // Service request
        serviceRequest.setCode(code);
        serviceRequest.setCategory(listOf(category));
        serviceRequest.setSubject(subject);
        serviceRequest.setEncounter(encounter);            
        serviceRequest.setAuthoredOn(ngayYeuCau);
        serviceRequest.setRequester(CanboYteDTO.toRef(bacSiYeuCau));
        serviceRequest.setOrderDetail(listOf(createCodeableConcept(noiDungYeuCau)));
        
        // Diagnostic report
        diagnosticReport.setCode(code);
        diagnosticReport.setCategory(listOf(category));
        diagnosticReport.setSubject(subject);
        diagnosticReport.setEncounter(encounter);
        diagnosticReport.setPerformer(listOf(CanboYteDTO.toRef(nguoiVietBaoCao)));
        diagnosticReport.setResultsInterpreter(listOf(CanboYteDTO.toRef(nguoiDanhGiaKetQua)));            
        diagnosticReport.setIssued(ngayGioBaoCao);
        diagnosticReport.setConclusion(ketLuan);
        
        // Observations
        var observations = new ArrayList<Observation>();
        if(dsKetQuaXetNghiem != null) {
            for(var xnkq : dsKetQuaXetNghiem) {
                var obs = new Observation();
                obs.setSubject(serviceRequest.getSubject());
                obs.setEncounter(serviceRequest.getEncounter());

                obs.setCode(DanhMuc.toConcept(xnkq.dmChiSoXetNghiem, CodeSystemValue.CHI_SO_XET_NGHIEM));
                
                var interpConcept = DanhMuc.toConcept(xnkq.dmDichKetQuaXetNghiem, CodeSystemValue.DICH_KET_QUA_XET_NGHIEM);
                obs.addInterpretation(interpConcept);
                
                if(xnkq.dmChiSoXetNghiem != null) {
                    var chiSoBt = (String) xnkq.dmChiSoXetNghiem.extra.get("chisobt");
                    if(chiSoBt != null) {
                        String[] arr = chiSoBt.split("-");
                        if(arr.length == 2) {
                            double giaTriDuoi = Double.valueOf(arr[0].trim());
                            double giaTriTren = Double.valueOf(arr[1].trim());
                            var range = obs.addReferenceRange();
                            range.setLow(new Quantity(giaTriDuoi));
                            range.setHigh(new Quantity(giaTriTren));
                        }
                    }                    
                }
                
                obs.setValue(new StringType(xnkq.giaTriDo));
                obs.setIssued(ngayThucHien);
                obs.setPerformer(listOf(CanboYteDTO.toRef(bacSiThucHien)));
                observations.add(obs);
            }
        }
        
        return mapOf(
                "serviceRequest", serviceRequest,
                "procedure", procedure,
                "diagnosticReport", diagnosticReport,
                "observations", observations
             );
    }	
}
