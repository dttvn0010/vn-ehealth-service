package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseIdentifier {

    public String system;

    public String value;

    public Integer order;
    
    public CodeableConcept type;
    
    public BasePeriod period;
    
    public List<Extension> extension;
    
    public BaseReference assigner;

    public IdentifierUse identifierUse;
    
    
    public static Identifier toIdentifier(BaseIdentifier entity) {
        if(entity == null) return null;
        
        var identifier = new Identifier();
        identifier.setSystem(entity.system);
        identifier.setValue(entity.value);
        identifier.setType(entity.type);
        identifier.setExtension(entity.extension);
        identifier.setUse(entity.identifierUse);
        identifier.setPeriod(BasePeriod.toPeriod(entity.period));
        identifier.setAssigner(BaseReference.toReference(entity.assigner));

        return identifier;
    }
    
    public static List<Identifier> toIdentifierList(List<BaseIdentifier> entityList) {
        return transform(entityList, x -> toIdentifier(x));
    }
    
    public static BaseIdentifier fromIdentifier(Identifier identifier) {
        if(identifier == null) return null;
        
        var entity = new BaseIdentifier();
        
        entity.system = identifier.getSystem();
        entity.value = identifier.getValue();
        entity.type = identifier.getType();
        entity.identifierUse = identifier.getUse();
        entity.extension = identifier.getExtension();
        entity.period = BasePeriod.fromPeriod(identifier.getPeriod());
        entity.assigner = BaseReference.fromReference(identifier.getAssigner());
        
        return entity;
    }
    
    public static List<BaseIdentifier> fromIdentifierList(List<Identifier> identifierList) {
        return transform(identifierList, x -> fromIdentifier(x));
    }
    
}
