package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.List;



import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class ObservationComponentEntity {
    public CodeableConcept code;
    public Type value;
    public CodeableConcept dataAbsentReason;
    public List<CodeableConcept> interpretation;
    public List<ObservationReferenceRangeEntity> referenceRange;
    
    public static ObservationComponentEntity fromObservationComponentComponent(ObservationComponentComponent obj) {
        if(obj == null) return null;
        
        var ent = new ObservationComponentEntity();
        ent.code = obj.getCode();
        ent.value = obj.getValue();
        ent.dataAbsentReason = obj.getDataAbsentReason();
        ent.interpretation = obj.getInterpretation();
        ent.referenceRange = transform(obj.getReferenceRange(), ObservationReferenceRangeEntity::fromObservationReferenceRangeComponent);
        return ent;
    }
    
    public static ObservationComponentComponent toObservationComponentComponent(ObservationComponentEntity ent) {
        if(ent == null) return null;
        
        var obj = new ObservationComponentComponent();
        obj.setCode(ent.code);
        obj.setValue(ent.value);
        obj.setDataAbsentReason(ent.dataAbsentReason);
        obj.setInterpretation(ent.interpretation);
        obj.setReferenceRange(transform(ent.referenceRange,  ObservationReferenceRangeEntity::toObservationReferenceRangeComponent));
        
        return obj;
    }
}
