package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;

import org.hl7.fhir.r4.model.ImagingStudy.ImagingStudySeriesComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

public class ImagingStudySeriesEntity {
    public String uid;
    public int number;
    public BaseCoding modality;
    public String description;
    public int numberOfInstances;
    //public String availability;
    public List<BaseReference> endpoint;
    public BaseCoding bodySite;
    public BaseCoding laterality;
    public Date started;
    public List<ImagingStudySeriesPerformerEntity> performer;
    public List<ImagingStudySeriesInstanceEntity> instance;
    
    public static ImagingStudySeriesEntity fromImagingStudySeriesComponent(ImagingStudySeriesComponent obj) {
        if(obj == null) return null;
        
        var ent = new ImagingStudySeriesEntity();
        
        ent.uid = obj.getUid();
        ent.number = obj.getNumber();
        ent.modality = BaseCoding.fromCoding(obj.getModality());
        ent.description = obj.getDescription();
        ent.numberOfInstances = obj.getNumberOfInstances();
        ent.endpoint = BaseReference.fromReferenceList(obj.getEndpoint());
        ent.bodySite = BaseCoding.fromCoding(obj.getBodySite());
        ent.laterality = BaseCoding.fromCoding(obj.getLaterality());
        ent.started = obj.getStarted();
        ent.performer = transform(obj.getPerformer(),ImagingStudySeriesPerformerEntity::fromImagingStudySeriesPerformerComponent);
        ent.instance = transform(obj.getInstance(), ImagingStudySeriesInstanceEntity::fromImagingStudySeriesInstanceComponent);
        
        return ent;
    }
    
    public static ImagingStudySeriesComponent toImagingStudySeriesComponent(ImagingStudySeriesEntity ent) {
        if(ent == null) return null;
        
        var obj = new ImagingStudySeriesComponent();        
        
        obj.setUid(ent.uid);
        obj.setNumber(ent.number);
        obj.setModality(BaseCoding.toCoding(ent.modality));
        obj.setDescription(ent.description);
        obj.setNumberOfInstances(ent.numberOfInstances);
        obj.setEndpoint(BaseReference.toReferenceList(ent.endpoint));
        obj.setBodySite(BaseCoding.toCoding(ent.bodySite));
        obj.setLaterality(BaseCoding.toCoding(ent.laterality));
        obj.setStarted(ent.started);
        obj.setPerformer(transform(ent.performer, ImagingStudySeriesPerformerEntity::toImagingStudySeriesPerformerComponent));
        obj.setInstance(transform(ent.instance,  ImagingStudySeriesInstanceEntity::toImagingStudySeriesInstanceComponent));
        
        return obj;
    }
}
