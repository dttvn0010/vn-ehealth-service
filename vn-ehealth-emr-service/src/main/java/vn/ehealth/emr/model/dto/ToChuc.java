package vn.ehealth.emr.model.dto;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResourceType;

public abstract class ToChuc extends BaseModelDTO {

    public String ma;
    public String ten;
    
    public ToChuc() {
        super();
    }
    
    public ToChuc(Organization obj) {
        super(obj);
        if(obj != null) {
            this.ma = obj.hasIdentifier()? obj.getIdentifierFirstRep().getValue() : "";
            this.ten = obj.hasName() ? obj.getName() : "";
            fromFhir(obj);
        }
    }
    
    public abstract Organization toFhir();
    protected abstract void fromFhir(Organization obj);
    
    @Override
    public ResourceType getType() {
        return ResourceType.Organization;
    }
}
