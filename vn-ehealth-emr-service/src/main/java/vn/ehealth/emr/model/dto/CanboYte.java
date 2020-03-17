package vn.ehealth.emr.model.dto;

import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.DbUtils;
import static vn.ehealth.emr.utils.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;

public class CanboYte extends BaseModelDTO {
    public String ten;
    public String chungChiHanhNghe;
    
    public CanboYte() {
        super();
    }
    
    public CanboYte(Practitioner obj) {
        super(obj);
        if(obj == null) return;
        
        this.ten = obj.hasName()? obj.getNameFirstRep().getText() : "";        
        this.chungChiHanhNghe = obj.hasIdentifier()? obj.getIdentifierFirstRep().getValue() : "";
    }
        
    public static CanboYte fromFhir(Practitioner obj) {
        if(obj == null) return null;
        return new CanboYte(obj);
    }
    
    public static CanboYte fromReference(Reference ref) {
        if(ref != null && ref.hasReference()) {
            var ent = DbUtils.getPractitionerDao().read(new IdType(ref.getReference()));
            return fromFhir(ent);
        }
        
        return null;
    }
    
    public static Practitioner toFhir(CanboYte dto) {
        if(dto == null) return null;
        
        var obj = DbUtils.getPractitionerDao().read(new IdType(dto.id));
        if(obj == null) {
            obj = new Practitioner();
        }
        
        obj.addName(createHumanName(dto.ten));
        obj.setIdentifier(listOf(createIdentifier(dto.chungChiHanhNghe, CodeSystemValue.CHUNG_CHI_HANH_NGHE)));
        
        return obj;
    }
}
