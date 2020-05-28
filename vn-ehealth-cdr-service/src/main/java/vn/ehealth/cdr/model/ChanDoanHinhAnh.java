package vn.ehealth.cdr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createCodeableConcept;

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
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.cdr.utils.CDRConstants.LoaiDichVuKT;

@JsonInclude(Include.NON_NULL)
@Document(collection = "chan_doan_hinh_anh")
public class ChanDoanHinhAnh extends DichVuKyThuat {

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
    
    public DanhMuc dmLoaiChanDoanHinhAnh;    
    public DanhMuc dmChanDoanHinhAnh;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public CanboYte bacSiChuyenKhoa;
    public String loiDan;
    public String ketQua;
        
    public List<FileDinhKem> dsFileDinhKemCdha = new ArrayList<>();
    
    @JsonIgnore
    public CodeableConcept getCategory() {
        return createCodeableConcept(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                MessageUtils.get("text.CT"), 
                CodeSystemValue.DIAGNOSTIC_SERVICE_SECTIONS);
    }

    @JsonIgnore
    public CodeableConcept getCode() {
        return DanhMuc.toConcept(dmChanDoanHinhAnh, CodeSystemValue.DICH_VU_KY_THUAT);
    }

    @Override
    public Map<String, Object> toFhir(Encounter enc) {
        if(enc == null) return null;
        
        var resources = toFhirCommon(enc);
        
        var procedure = (Procedure) resources.get("procedure");
        var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
        var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
      
        // Procedure
        if(procedure != null) {
            procedure.setAsserter(CanboYte.toRef(bacSiChuyenKhoa));
            
            if(ngayThucHien != null) {
                procedure.setPerformed(new DateTimeType(ngayThucHien));
            }
                    
            procedure.setOutcome(createCodeableConcept(ketQua));
            procedure.setFollowUp(listOf(createCodeableConcept(loiDan)));
        }
            
        return mapOf(
                    "serviceRequest", serviceRequest,
                    "procedure", procedure,
                    "diagnosticReport", diagnosticReport
                 );
    }
}
