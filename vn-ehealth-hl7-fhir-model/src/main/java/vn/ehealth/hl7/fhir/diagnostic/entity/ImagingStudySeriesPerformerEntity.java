package vn.ehealth.hl7.fhir.diagnostic.entity;

import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesPerformerComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;

public class ImagingStudySeriesPerformerEntity {

    public BaseReference actor;
    public BaseCodeableConcept function;
    
    public static ImagingStudySeriesPerformerEntity fromImagingStudySeriesPerformerComponent(ImagingStudySeriesPerformerComponent obj) {
        if(obj == null) return null;
        
        var ent = new ImagingStudySeriesPerformerEntity();
        ent.actor = BaseReference.fromReference(obj.getActor());
        ent.function = BaseCodeableConcept.fromCodeableConcept(obj.getFunction());
        
        return ent;
    }
    
    public static ImagingStudySeriesPerformerComponent toImagingStudySeriesPerformerComponent(ImagingStudySeriesPerformerEntity ent) {
        if(ent == null) return null;
        
        var obj = new ImagingStudySeriesPerformerComponent();
        obj.setActor(BaseReference.toReference(ent.actor));
        obj.setFunction(BaseCodeableConcept.toCodeableConcept(ent.function));
        
        return obj;
    }
}
