package vn.ehealth.cdr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Procedure.ProcedurePerformerComponent;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "phau_thuat_thu_thuat")
public class PhauThuatThuThuat extends DichVuKyThuat {

    @Id public ObjectId id;        
    
    public ObjectId hoSoBenhAnId;
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getHoSoBenhAnId() {
        return ObjectIdUtil.idToString(hoSoBenhAnId);
    }
    
    public void setHoSoBenhAnId(String hoSoBenhAnId) {
        this.hoSoBenhAnId = ObjectIdUtil.stringToId(hoSoBenhAnId);            
    }

    public String getBenhNhanId() {
        return ObjectIdUtil.idToString(benhNhanId);
    }

    public void setBenhNhanId(String benhNhanId) {
        this.benhNhanId = ObjectIdUtil.stringToId(benhNhanId);
    }
    
    public String getCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(coSoKhamBenhId);
    }
    
    public void setCoSoKhamBenhId(String coSoKhamBenhId) {
        this.coSoKhamBenhId = ObjectIdUtil.stringToId(coSoKhamBenhId);
    }
    
    public int trangThai; 
    public String idhis;
    
    public List<DanhMuc> dsDmMaBenhChanDoanSau = new ArrayList<>();
    public List<DanhMuc> dsDmMaBenhChanDoanTruoc = new ArrayList<>();
    
    public DanhMuc dmMaBenhChanDoanSau;
    public DanhMuc dmMaBenhChanDoanTruoc;
    
    public DanhMuc dmPhauThuatThuThuat;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioPttt;
    public CanboYte bacSiThucHien;
    public CanboYte bacSiGayMe;
    public CanboYte thuKyGhiChep;
    
    public String chiDinhPttt;
    public String phuongPhapVoCam;
    public String luocDoPttt;
    public String trinhTuPttt;
    public String ghiChu;    
    
    public String moTaChanDoanTruocPttt;
    public String moTaChanDoanSauPttt;
        
    public List<FileDinhKem> dsFileDinhKemPttt = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class ThanhVienPttt {

        public DanhMuc dmVaiTro;        
        public CanboYte bacsipttt;
        
        public static ProcedurePerformerComponent toPerformer(ThanhVienPttt dto) {
            if(dto == null) return null;
            var performer = new ProcedurePerformerComponent();
            performer.setFunction(DanhMuc.toConcept(dto.dmVaiTro, CodeSystemValue.VAI_TRO_PTTT));
            performer.setActor(CanboYte.toRef(dto.bacsipttt));
            return performer;
        }
    }
    
    public List<ThanhVienPttt> dsThanhVienPttt = new ArrayList<>();
    
    @Override
    protected CodeableConcept getCategory() {
        return createCodeableConcept(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                MessageUtils.get("text.SUR"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    }

    @Override
    protected CodeableConcept getCode() {
        return DanhMuc.toConcept(dmPhauThuatThuThuat, CodeSystemValue.DICH_VU_KY_THUAT);
    }

    @Override
    public Map<String, Object> toFhir(Encounter enc) {
        var resources = toFhirCommon(enc);
        
        var procedure = (Procedure) resources.get("procedure");
        var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
        var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
        
        if(procedure != null) {
            procedure.setAsserter(CanboYte.toRef(bacSiThucHien));
            procedure.setRecorder(CanboYte.toRef(thuKyGhiChep));
            
            if(ngayGioPttt != null) {
                procedure.setPerformed(new DateTimeType(ngayGioPttt));
            }
                    
            procedure.setNote(listOf(createAnnotation(ghiChu)));
            procedure.setExtension(listOf(createExtension(ExtensionURL.TRINH_TU_PTTT, trinhTuPttt)));
            procedure.setPerformer(transform(dsThanhVienPttt, x -> ThanhVienPttt.toPerformer(x)));
        }
            
        return mapOf(
                    "serviceRequest", serviceRequest,
                    "procedure", procedure,
                    "diagnosticReport", diagnosticReport
                );
    }
}
