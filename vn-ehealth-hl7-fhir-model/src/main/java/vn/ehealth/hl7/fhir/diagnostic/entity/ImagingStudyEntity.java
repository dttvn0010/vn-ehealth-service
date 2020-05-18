package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "imagingStudy")
//@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ImagingStudyEntity extends BaseResource {
    
    public static class ImagingStudySeriesPerformer extends BaseBackboneElement {
        public BaseReference actor;
        public BaseCodeableConcept function;
    }
    
    public static class ImagingStudySeriesInstance extends BaseBackboneElement {
        public String uid;
        public int number;
        public BaseCoding sopClass;
        public String title;
    }
    
    public static class ImagingStudySeries extends BaseBackboneElement {
        public String uid;
        public int number;
        public BaseCoding modality;
        public String description;
        public int numberOfInstances;
        public List<BaseReference> endpoint;
        public BaseCoding bodySite;
        public BaseCoding laterality;
        public List<BaseReference> specimen;
        public Date started;
        public List<ImagingStudySeriesPerformer> performer;
        public List<ImagingStudySeriesInstance> instance;
    }
    
    @Id
    @Indexed(name = "_id_")
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<BaseCoding> modality;
    public BaseReference subject;
    public BaseReference encounter;
    public Date started;
    public List<BaseReference> basedOn;
    public BaseReference referrer;
    public List<BaseReference> interpreter;
    public List<BaseReference> endpoint;
    public int numberOfSeries;
    public int numberOfInstances;
    public BaseReference procedureReference;
    public List<BaseCodeableConcept> procedureCode;
    public BaseReference location;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseAnnotation> note;
    public String description;
    public List<ImagingStudySeries> series;
}
