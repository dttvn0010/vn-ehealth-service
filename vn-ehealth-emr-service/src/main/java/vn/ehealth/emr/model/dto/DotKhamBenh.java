package vn.ehealth.emr.model.dto;

import java.util.Date;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.MessageUtils;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.EncounterType;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class DotKhamBenh extends BaseModelDTO {    
    public BaseRef patient;
    public BaseRef serviceProvider;
    public String maYte;
    public DanhMuc dmLoaiKham;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public DotKhamBenh() {
        super();
    }
    
    public DotKhamBenh(Encounter obj) {
        super(obj);
        
        if(obj == null) return;
        
        this.patient = new BaseRef(obj.getSubject());  
        this.patient.data = BenhNhan.fromFhir((Patient) this.patient.resource);
        
        
        this.serviceProvider = new BaseRef(obj.getServiceProvider());
        this.serviceProvider.data = CoSoKhamBenh.fromFhir((Organization) this.serviceProvider.resource);
        
        this.maYte = obj.hasIdentifier()? obj.getIdentifierFirstRep().getValue(): "";
        
        if(obj.hasType()) {
            var concept = findConceptBySystem(obj.getType(), CodeSystemValue.LOAI_KHAM_BENH);
            this.dmLoaiKham = new DanhMuc(concept);
        }
        
        if(obj.hasPeriod()) {
            this.ngayGioVao = obj.getPeriod().getStart();
            this.ngayGioKetThucDieuTri = obj.getPeriod().getEnd();
        }
    }
    
    public static DotKhamBenh fromFhir(Encounter obj) {
        if(obj == null) return null;
        return new DotKhamBenh(obj);
    }
    
    public static Encounter toFhir(DotKhamBenh dto) {
        if(dto == null) return null;
        var obj = new Encounter();
        obj.setId(dto.id);
        obj.setIdentifier(listOf(createIdentifier(dto.maYte, IdentifierSystem.MA_HO_SO)));
        
        if(dto.patient != null) {
        	obj.setSubject(createReference(ResourceType.Patient, dto.patient.id));
        }
        
        if(dto.serviceProvider != null) {
        	obj.setServiceProvider(createReference(ResourceType.Organization, dto.serviceProvider.id));
        }
        
        obj.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        
        var loaiBenhAn = DanhMuc.toConcept(dto.dmLoaiKham, CodeSystemValue.LOAI_KHAM_BENH);
        
        var encType = createCodeableConcept(EncounterType.DOT_KHAM, 
			                MessageUtils.get("text.HSBA"), 
			                CodeSystemValue.ENCOUTER_TYPE);

    	obj.setType(listOf(loaiBenhAn, encType));
    	

        return obj;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.Encounter;
    }
}
