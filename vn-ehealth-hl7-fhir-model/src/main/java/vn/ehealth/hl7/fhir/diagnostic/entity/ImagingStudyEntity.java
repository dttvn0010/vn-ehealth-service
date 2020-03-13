package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;


import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ImagingStudy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "imagingStudy")
public class ImagingStudyEntity extends BaseResource {
    @Id
    public ObjectId id;
    //public String uid;
    //public BaseIdentifier accession;
    public List<BaseIdentifier> identifier;
    //public String availability;
    public List<BaseCoding> modalityList;
    //public BaseReference patient;
    //public BaseReference context;
    public Date started;
    public List<BaseReference> basedOn;
    public BaseReference referrer;
    public BaseReference encounter;
    public List<BaseReference> interpreter;
    public List<BaseReference> endpoint;
    public int numberOfSeries;
    public int numberOfInstances;
    public BaseReference procedureReference;
    public List<BaseCodeableConcept> procedureCode;
    public List<BaseCodeableConcept> reasonCode;
    public String description;
    public List<ImagingStudySeriesEntity> series;
    
    
    public static ImagingStudyEntity fromImagingStudy(ImagingStudy obj) {
        if(obj == null) return null;
        
        var ent = new ImagingStudyEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.modalityList = BaseCoding.fromCodingList(obj.getModality());
        ent.started = obj.getStarted();
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.referrer = BaseReference.fromReference(obj.getReferrer());
        ent.encounter = BaseReference.fromReference(obj.getEncounter());
        ent.interpreter = BaseReference.fromReferenceList(obj.getInterpreter());
        ent.endpoint = BaseReference.fromReferenceList(obj.getEndpoint());
        ent.numberOfSeries = obj.getNumberOfSeries();
        ent.numberOfInstances = obj.getNumberOfInstances();
        ent.procedureReference = BaseReference.fromReference(obj.getProcedureReference());
        ent.procedureCode = BaseCodeableConcept.fromCodeableConcept(obj.getProcedureCode());
        ent.reasonCode = BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode());
        ent.description = obj.getDescription();
        ent.series = transform(obj.getSeries(), ImagingStudySeriesEntity::fromImagingStudySeriesComponent);
        
        return ent;
    }
    
    public static ImagingStudy toImagingStudy(ImagingStudyEntity ent) {
        if(ent == null) return null;
        
        var obj = new ImagingStudy();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setModality(BaseCoding.toCodingList(ent.modalityList));
        obj.setStarted(ent.started);
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setReferrer(BaseReference.toReference(ent.referrer));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setInterpreter(BaseReference.toReferenceList(ent.interpreter));
        obj.setEndpoint(BaseReference.toReferenceList(ent.endpoint));
        obj.setNumberOfSeries(ent.numberOfSeries);
        obj.setNumberOfInstances(ent.numberOfInstances);
        obj.setProcedureReference(BaseReference.toReference(ent.procedureReference));
        obj.setProcedureCode(BaseCodeableConcept.toCodeableConcept(ent.procedureCode));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setDescription(ent.description);
        obj.setSeries(transform(ent.series,ImagingStudySeriesEntity::toImagingStudySeriesComponent));
        
        return obj;
    }
}
