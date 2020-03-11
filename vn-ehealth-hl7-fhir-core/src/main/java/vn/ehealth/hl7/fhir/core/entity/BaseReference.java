package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Reference;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseReference {
    public String reference;
    public BaseIdentifier identifier;
    public String display;
 
    public static BaseReference fromReference(Reference ref) {
        if(ref == null) return null;
        
        var entity = new BaseReference();
        entity.reference = ref.getReference();
        entity.identifier = BaseIdentifier.fromIdentifier(ref.getIdentifier());
        entity.display = ref.getDisplay();
        
        return entity;
    }
    
    public static List<BaseReference> fromReferenceList(List<Reference> refList) {
        return transform(refList, x -> fromReference(x));
    }

    public static Reference toReference(BaseReference entity) {
        if(entity == null) return null;
        
        var ref = new Reference();
        ref.setReference(entity.reference);
        ref.setIdentifier(BaseIdentifier.toIdentifier(entity.identifier));
        ref.setDisplay(entity.display);
        
        return ref;
    }
    
    public static List<Reference> toReferenceList(List<BaseReference> entityList) {
        return transform(entityList, x -> toReference(x));        
    }
    
}
