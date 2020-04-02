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
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.utils.MessageUtils;
import vn.ehealth.cdr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "xet_nghiem")
public class XetNghiem {
    
    @Id public ObjectId id;        
    public ObjectId hoSoBenhAnId;    
    public ObjectId benhNhanId;
    public ObjectId coSoKhamBenhId;
    public int trangThai;
    public String idhis;
    
    public DanhMuc dmLoaiXetNghiem;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayYeuCau;
    
    public CanboYte bacSiYeuCau;
    public String noiDungYeuCau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayThucHien;
    
    public CanboYte bacSiXetNghiem;    
    public String nhanXet;
    
    public List<FileDinhKem> dsFileDinhKemXetNghiem = new ArrayList<>();
    
    @JsonInclude(Include.NON_NULL)
    public static class XetNghiemDichVu extends DichVuKyThuat {
        
        public Date ngayThucHien;
        
        public CanboYte bacSiXetNghiem;
                
        public DanhMuc dmXetNghiem;
        public List<XetNghiemKetQua> dsKetQuaXetNghiem = new ArrayList<XetNghiemKetQua>();
        
        @JsonInclude(Include.NON_NULL)
        public static class XetNghiemKetQua {
            public DanhMuc dmDichKetQuaXetNghiem;
            
            public DanhMuc dmChiSoXetNghiem;

            public String giaTriDo;
        }

        @Override
        protected CodeableConcept getCategory() {
            return createCodeableConcept(LoaiDichVuKT.XET_NGHIEM, 
                    MessageUtils.get("text.LAB"), 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        }

        @Override
        protected CodeableConcept getCode() {
            return DanhMuc.toConcept(dmXetNghiem, CodeSystemValue.DICH_VU_KY_THUAT);
        }

        @Override
        public Map<String, Object> toFhir(Encounter enc) {
            var resources = toFhirCommon(enc);
            
            var procedure = (Procedure) resources.get("procedure");
            var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
            var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
            
            // Procedure
            if(procedure != null) {
                procedure.setAsserter(CanboYte.toRef(bacSiXetNghiem));
                
                if(ngayThucHien != null) {
                    procedure.setPerformed(new DateTimeType(ngayThucHien));
                }
            }
            
            // Observations
            var observations = new ArrayList<Observation>();
            if(dsKetQuaXetNghiem != null) {
                for(var xnkq : dsKetQuaXetNghiem) {
                    var obs = new Observation();
                    obs.setSubject(serviceRequest.getSubject());
                    obs.setEncounter(serviceRequest.getEncounter());
                    
                    obs.setCode(DanhMuc.toConcept(xnkq.dmChiSoXetNghiem, CodeSystemValue.CHI_SO_XET_NGHIEM));
                    obs.setValue(new StringType(xnkq.giaTriDo));
                    obs.setIssued(ngayThucHien);
                    obs.setPerformer(listOf(CanboYte.toRef(bacSiXetNghiem)));
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
    
    public List<XetNghiemDichVu> dsDichVuXetNghiem = new ArrayList<>();
    
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
}
