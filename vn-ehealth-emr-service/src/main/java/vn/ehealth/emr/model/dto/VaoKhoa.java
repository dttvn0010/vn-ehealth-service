package vn.ehealth.emr.model.dto;

import java.util.Date;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonFormat;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class VaoKhoa extends BaseModelDTO {
    public String patientId;
    public String falcultyId;
    public String episodeOfCareId;
        
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioVao;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayGioKetThucDieuTri;
    
    public VaoKhoa() {
        super();
    }
    
    public VaoKhoa(Encounter obj) {
        super(obj);
        if(obj == null) return;
        
        this.patientId = idFromRef(obj.getSubject());
        this.falcultyId = idFromRef(obj.getServiceProvider());
        
        this.ngayGioVao = obj.hasPeriod()? obj.getPeriod().getStart() : null;
        this.ngayGioKetThucDieuTri = obj.hasPeriod()? obj.getPeriod().getEnd() : null;
    }
    
    public static VaoKhoa fromFhir(Encounter obj) {
        if(obj == null) return null;
        return new VaoKhoa(obj);
    }
    
    public static Encounter toFhir(VaoKhoa dto) {
        if(dto == null) return null;
        var ent = new Encounter();
        
        ent.setSubject(createReference(ResourceType.Patient, dto.patientId));
        ent.setEpisodeOfCare(listOf(createReference(ResourceType.EpisodeOfCare, dto.episodeOfCareId)));
        ent.setServiceProvider(createReference(ResourceType.Organization, dto.falcultyId));
        ent.setPeriod(createPeriod(dto.ngayGioVao, dto.ngayGioKetThucDieuTri));
        
        return ent;
    }

    @Override
    public ResourceType getType() {
        return ResourceType.Encounter;
    }
    
}
