package vn.ehealth.emr.model.dto;

import java.util.Date;
import org.hl7.fhir.r4.model.EpisodeOfCare;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonFormat;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class DotKhamBenh extends BaseModelDTO {    
    public String patientId;
    public String serviceProviderId;
    
    public String maYte;
    public DanhMuc dmLoaiKham;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public DotKhamBenh() {
        super();
    }
    
    public DotKhamBenh(EpisodeOfCare obj) {
        super(obj);
        
        if(obj == null) return;
        
        this.patientId = idFromRef(obj.getPatient());
        this.serviceProviderId = idFromRef(obj.getManagingOrganization());
        
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
    
    public static DotKhamBenh fromFhir(EpisodeOfCare obj) {
        if(obj == null) return null;
        return new DotKhamBenh(obj);
    }
    
    public static EpisodeOfCare toFhir(DotKhamBenh dto) {
        if(dto == null) return null;
        var episode = new EpisodeOfCare();
        episode.setId(dto.id);
        episode.setIdentifier(listOf(createIdentifier(dto.maYte, IdentifierSystem.MA_HO_SO)));
        episode.setType(listOf(DanhMuc.toConcept(dto.dmLoaiKham, CodeSystemValue.LOAI_KHAM_BENH)));
        episode.setPatient(createReference(ResourceType.Patient, dto.patientId));
        episode.setManagingOrganization(createReference(ResourceType.Organization, dto.serviceProviderId));
        episode.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        return episode;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.EpisodeOfCare;
    }
}
