package vn.ehealth.emr.model;

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
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_xet_nghiem")
public class EmrXetNghiem {
    
    @Id public ObjectId id;        
    public ObjectId emrHoSoBenhAnId;    
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    public EmrDmContent emrDmLoaiXetNghiem;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayyeucau;
    
    public EmrCanboYte bacsiyeucau;
    public String noidungyeucau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaythuchien;
    
    public EmrCanboYte bacsixetnghiem;    
    public String nhanxet;
    
    public List<EmrFileDinhKem> emrFileDinhKemXetNghiems = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class EmrXetNghiemDichVu extends EmrDichVuKyThuat {
        
        public Date ngaythuchien;
        
        public EmrCanboYte bacsixetnghiem;
                
        public EmrDmContent emrDmXetNghiem;
        public List<EmrXetNghiemKetQua> emrXetNghiemKetQuas = new ArrayList<EmrXetNghiemKetQua>();
        
        @JsonInclude(Include.NON_NULL)
        public static class EmrXetNghiemKetQua {
            public EmrDmContent emrDmDichKetQuaXetNghiem;
            
            public EmrDmContent emrDmChiSoXetNghiem;

            public String giatrido;
        }

        @Override
        protected CodeableConcept getCategory() {
            return createCodeableConcept(LoaiDichVuKT.XET_NGHIEM, 
                    MessageUtils.get("text.LAB"), 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        }

        @Override
        protected CodeableConcept getCode() {
            return EmrDmContent.toConcept(emrDmXetNghiem, CodeSystemValue.DICH_VU_KY_THUAT);
        }

        @Override
        public Map<String, Object> toFhir(Encounter enc) {
            var resources = toFhirCommon(enc);
            
            var procedure = (Procedure) resources.get("procedure");
            var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
            var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
            
            // Procedure
            if(procedure != null) {
                procedure.setAsserter(EmrCanboYte.toRef(bacsixetnghiem));
                
                if(ngaythuchien != null) {
                    procedure.setPerformed(new DateTimeType(ngaythuchien));
                }
            }
            
            // Observations
            var observations = new ArrayList<Observation>();
            if(emrXetNghiemKetQuas != null) {
                for(var xnkq : emrXetNghiemKetQuas) {
                    var obs = new Observation();
                    obs.setSubject(serviceRequest.getSubject());
                    obs.setEncounter(serviceRequest.getEncounter());
                    
                    obs.setCode(EmrDmContent.toConcept(xnkq.emrDmChiSoXetNghiem, CodeSystemValue.CHI_SO_XET_NGHIEM));
                    obs.setValue(new StringType(xnkq.giatrido));
                    observations.add(obs);
                }
            }
        
            return mapOf(
                        "serviceRequest", serviceRequest,
                        "procedure", procedure,
                        "observations", observations,
                        "diagnosticReport", diagnosticReport
                     );
        } 
    }
    
    public List<EmrXetNghiemDichVu> emrXetNghiemDichVus = new ArrayList<>();
    
    public String getId() { 
        return ObjectIdUtil.idToString(id); 
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getEmrHoSoBenhAnId() {
        return ObjectIdUtil.idToString(emrHoSoBenhAnId);
    }
    
    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = ObjectIdUtil.stringToId(emrHoSoBenhAnId);            
    }

    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
}
