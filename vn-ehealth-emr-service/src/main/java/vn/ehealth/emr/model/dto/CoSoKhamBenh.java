package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.Constants.CodeSystem;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

public class CoSoKhamBenh extends BaseModelDTO {
    
    public String ma;
    public String ten;
    
    public CoSoKhamBenh() {
        super();
    }
    
    public CoSoKhamBenh(LocationEntity ent) {
        super(ent);
        if(ent == null) return;
        
        this.ma = ent.getIdentifier();
        this.ten = ent.name;
    }
    
    public static CoSoKhamBenh fromEntity(LocationEntity ent) {
        if(ent == null) return null;
        return new CoSoKhamBenh(ent);        
    }
    
    public static CoSoKhamBenh fromReference(BaseReference ref) {
        if(ref != null && ref.reference != null) {
            var ent = ServiceFactory.getLocationService().getByFhirId(ref.reference).orElseThrow();
            return fromEntity(ent);
        }
        return null;        
    }
    
    public static LocationEntity toEntity(CoSoKhamBenh dto) {
        if(dto == null) return null;
        var ent = ServiceFactory.getLocationService().getByFhirId(dto.fhirId).orElse(null);
        
        if(ent == null) {
            ent = new LocationEntity();
            ent.fhirId = ent.fhirId = StringUtil.generateUID();
        }
        
        ent.identifier = listOf(new BaseIdentifier(dto.ma, CodeSystem.CO_SO_KHAM_BENH));
        ent.name = dto.ten;
        return ent;
    }
}
