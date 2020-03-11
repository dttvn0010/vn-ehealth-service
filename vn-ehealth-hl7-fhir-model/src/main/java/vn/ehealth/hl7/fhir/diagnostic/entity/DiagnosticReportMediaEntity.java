package vn.ehealth.hl7.fhir.diagnostic.entity;

import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportMediaComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;



public class DiagnosticReportMediaEntity {

    public String comment;
    public BaseReference link;
    
    public static DiagnosticReportMediaEntity fromDiagnosticReportMediaComponent(DiagnosticReportMediaComponent obj) {
        if(obj == null) return null;
        
        var ent = new DiagnosticReportMediaEntity();
        
        ent.comment = obj.getComment();
        ent.link = BaseReference.fromReference(obj.getLink());
        
        return ent;
    }
    
    public static DiagnosticReportMediaComponent toDiagnosticReportMediaComponent(DiagnosticReportMediaEntity ent) {
        if(ent == null) return null;
        
        var obj = new DiagnosticReportMediaComponent();
        obj.setComment(ent.comment);
        obj.setLink(BaseReference.toReference(ent.link));
        
        return obj;
    }
}
