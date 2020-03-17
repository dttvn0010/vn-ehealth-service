package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.provider.entity.PractitionerEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class CanboYte extends BaseModelDTO {
    public String ten;
    public String chungChiHanhNghe;
    
    public CanboYte() {
        super();
    }
    
    public CanboYte(PractitionerEntity ent) {
        super(ent);
        if(ent == null) return;
        
        this.ten = ent.getName();        
        this.chungChiHanhNghe = ent.getIdentifier();
    }
        
    public static CanboYte fromEntity(PractitionerEntity ent) {
        if(ent == null) return null;
        return new CanboYte(ent);
    }
    
    public static CanboYte fromReference(BaseReference ref) {
        if(ref != null && ref.reference != null) {
            var ent = ServiceFactory.getPractitionerService().getByFhirId(ref.reference).orElseThrow();
            return fromEntity(ent);
        }
        
        return null;
    }
    
    public static PractitionerEntity toEntity(CanboYte dto) {
        if(dto == null) return null;
        
        var ent = ServiceFactory.getPractitionerService().getByFhirId(dto.fhirId).orElse(null);
        if(ent == null) {
            ent = new PractitionerEntity();
            ent.fhirId = StringUtil.generateUID(); 
        }
        
        ent.name = listOf(new BaseHumanName(dto.ten));
        ent.identifier = listOf(new BaseIdentifier(dto.chungChiHanhNghe, CodeSystem.CHUNG_CHI_HANH_NGHE));
        
        return ent;
    }
}
