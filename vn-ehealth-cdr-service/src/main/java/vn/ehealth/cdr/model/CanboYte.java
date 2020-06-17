package vn.ehealth.cdr.model;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;

@JsonInclude(Include.NON_NULL)
public class CanboYte {

    public String ten;
    public String chungChiHanhNghe;
    
    @JsonIgnore
    public ObjectId emrPersonId;
   
    public CanboYte() {
        
    }
    
    public CanboYte(String ten) {
        this.ten = ten;
    }
    
    public static Reference toRef(CanboYte dto) {
        if(dto != null) {
            var ref = new Reference();
            ref.setDisplay(dto.ten);
            return ref;
        }
        return null;
    }
    
    public static CanboYte fromFhir(Practitioner obj) {
        if(obj == null) return null;
        
        var dto = new CanboYte();
        dto.ten = obj.getNameFirstRep().getText();
        var identity = FhirUtil.findIdentifierBySystem(obj.getIdentifier(), IdentifierSystem.PRACTITIONER);
        if(identity != null) {
            dto.chungChiHanhNghe = identity.getValue();
        }
        
        return dto;        
    }
}
