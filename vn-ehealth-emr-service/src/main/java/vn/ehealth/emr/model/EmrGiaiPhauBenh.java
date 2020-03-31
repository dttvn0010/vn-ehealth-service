package vn.ehealth.emr.model;

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
import org.hl7.fhir.r4.model.Specimen;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.ExtensionURL;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_giai_phau_benh")
public class EmrGiaiPhauBenh extends EmrDichVuKyThuat {

    @Id public ObjectId id;
    
    public ObjectId emrHoSoBenhAnId;
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
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
    
    
    
    public int trangThai;
    public String idhis;
    
    public EmrDmContent emrDmGiaiPhauBenh;        
    public EmrDmContent emrDmLoaiGiaiPhauBenh;
    public EmrDmContent emrDmViTriLayMau;
    public EmrDmContent emrDmKetQuaGiaiPhauBenh;
    
    public EmrCanboYte bacsichuyenkhoa;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaythuchien;
    
    public String nhanxetdaithe;
    public String nhanxetvithe;
    
    public EmrDmContent emrDmChanDoanGiaiPhau;
    public String motachandoangiaiphau;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaylaymausinhthiet;
        
    public List<EmrFileDinhKem> emrFileDinhKemGpbs = new ArrayList<>();
    
    
    @Override
    protected CodeableConcept getCategory() {
        return createCodeableConcept(LoaiDichVuKT.GIAI_PHAU_BENH, 
                MessageUtils.get("text.XRC"), 
                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    }

    @Override
    protected CodeableConcept getCode() {
        return EmrDmContent.toConcept(emrDmGiaiPhauBenh, CodeSystemValue.DICH_VU_KY_THUAT);
    }

    @Override
    public Map<String, Object> toFhir(Encounter enc) {
        var resources = toFhirCommon(enc);
        
        var procedure = (Procedure) resources.get("procedure");
        var serviceRequest = (ServiceRequest) resources.get("serviceRequest");
        var diagnosticReport = (DiagnosticReport) resources.get("diagnosticReport");
        
        var specimen = new Specimen();
            
        // Specimen
        specimen.setSubject(procedure.getSubject()); 
        specimen.setReceivedTime(ngaythuchien);
        
        var collection = specimen.getCollection();
        collection.setCollector(EmrCanboYte.toRef(bacsichuyenkhoa));        
        collection.setBodySite(EmrDmContent.toConcept(emrDmViTriLayMau, CodeSystemValue.VI_TRI_MAU_SINH_THIET));
        collection.setCollected(new DateTimeType(ngaylaymausinhthiet));
        
        var nxDaiTheExt = createExtension(ExtensionURL.DAI_THE_GPB, nhanxetvithe);
        var nxViTheExt = createExtension(ExtensionURL.VI_THE_GPB, nhanxetdaithe);
        specimen.setExtension(listOf(nxDaiTheExt, nxViTheExt)); 
                    
        if(emrDmChanDoanGiaiPhau != null) {
            var concept = new CodeableConcept();
            concept.setText(motachandoangiaiphau);
            concept.setCoding(listOf(EmrDmContent.toCoding(emrDmChanDoanGiaiPhau, CodeSystemValue.ICD10)));
            diagnosticReport.getConclusionCode().add(concept);
        }
        
        return mapOf(
                    "procedure", procedure,
                    "serviceRequest", serviceRequest,
                    "specimen", specimen,
                    "diagnosticReport", diagnosticReport
                 );
    }
}
