package vn.ehealth.hl7.fhir.core.entity;

import java.util.List;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Identifier.IdentifierUse;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class BaseIdentifier {

    public String system;

    public String value;

    public Integer order;
    
    public BaseCodeableConcept type;
    
    public BasePeriod period;
    
    public List<BaseExtension> extension;
    
    public BaseReference assigner;

    public IdentifierUse identifierUse;
    
    
    public static Identifier toIdentifier(BaseIdentifier ent) {
        if(ent == null) return null;
        
        var obj = new Identifier();
        obj.setSystem(ent.system);
        obj.setValue(ent.value);
        obj.setType(BaseCodeableConcept.toCodeableConcept(ent.type));
        obj.setExtension(transform(ent.extension, BaseExtension::toExtension));
        obj.setUse(ent.identifierUse);
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setAssigner(BaseReference.toReference(ent.assigner));

        return obj;
    }
    
    public static List<Identifier> toIdentifierList(List<BaseIdentifier> entityList) {
        return transform(entityList, x -> toIdentifier(x));
    }
    
    public static BaseIdentifier fromIdentifier(Identifier obj) {
                
        if(obj == null) return null;
        
        var ent = new BaseIdentifier();
        
        ent.system = obj.hasSystem()? obj.getSystem() : null;
        ent.value = obj.hasValue()? obj.getValue() : null;
        ent.type = obj.hasType()? BaseCodeableConcept.fromCodeableConcept(obj.getType()) : null;
        ent.identifierUse =  obj.hasUse()? obj.getUse() : null;
        ent.extension = obj.hasExtension()? transform(obj.getExtension(), BaseExtension::fromExtension) : null;
        ent.period = obj.hasPeriod()? BasePeriod.fromPeriod(obj.getPeriod()) : null;
        ent.assigner = obj.hasAssigner()? BaseReference.fromReference(obj.getAssigner()) : null;
        
        return ent;
    }
    
    public static List<BaseIdentifier> fromIdentifierList(List<Identifier> identifierList) {
        return transform(identifierList, x -> fromIdentifier(x));
    }
    
}
