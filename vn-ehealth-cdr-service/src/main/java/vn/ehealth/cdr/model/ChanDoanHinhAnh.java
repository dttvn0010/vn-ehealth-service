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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_chan_doan_hinh_anh")
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
    public String idhis;
    
    public DanhMuc emrDmLoaiChanDoanHinhAnh;    
    public DanhMuc emrDmChanDoanHinhAnh;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaythuchien;
    
    public CanboYte bacsichuyenkhoa;
    public String loidan;
    public String ketqua;
        
    public List<FileDinhKem> emrFileDinhKemCdhas = new ArrayList<>();
    
    @Override
    protected CodeableConcept getCategory() {
        return createCodeableConcept(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                MessageUtils.get("text.CT"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    }

    @Override
    protected CodeableConcept getCode() {
        return DanhMuc.toConcept(emrDmChanDoanHinhAnh, CodeSystemValue.DICH_VU_KY_THUAT);
    }

    @Override
    public Map<String, Object> toFhir(Encounter enc) {

        var resources = toFhirCommon(enc);
        
        var procedure = (Procedure) resources.get("procedure");
        var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
        var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
      
        // Procedure
        if(procedure != null) {
            procedure.setAsserter(CanboYte.toRef(bacsichuyenkhoa));
            
            if(ngaythuchien != null) {
                procedure.setPerformed(new DateTimeType(ngaythuchien));
            }
                    
            procedure.setOutcome(createCodeableConcept(ketqua));
            procedure.setFollowUp(listOf(createCodeableConcept(loidan)));
        }
            
        return mapOf(
                    "serviceRequest", serviceRequest,
                    "procedure", procedure,
                    "diagnosticReport", diagnosticReport
                 );
    }
}