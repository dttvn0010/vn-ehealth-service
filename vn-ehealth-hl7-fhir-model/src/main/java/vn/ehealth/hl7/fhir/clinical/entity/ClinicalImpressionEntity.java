package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "clinicalImpression")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class ClinicalImpressionEntity extends BaseResource {

    public static class ClinicalImpressionInvestigation extends BaseBackboneElement{

        public BaseCodeableConcept code;
        public List<BaseReference> item;
    }
    
    public static class ClinicalImpressionFinding extends BaseBackboneElement {
        public BaseCodeableConcept itemCodeableConcept;
        public BaseReference itemReference;
        public String basis;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept statusReason;
    public BaseCodeableConcept code;
    public String description;
    public BaseReference subject;
    public BaseReference encounter;
    public BaseType effective;
    public Date date;
    public BaseReference assessor;
    public BaseReference previous;
    public List<BaseReference> problem;
    public List<ClinicalImpressionInvestigation> investigation;
    public List<String> protocol;
    public String summary;
    public List<ClinicalImpressionFinding> finding;
    public List<BaseCodeableConcept> prognosisCodeableConcept;
    public List<BaseReference> prognosisReference;
    public List<BaseReference> supportingInfo;
    public List<BaseAnnotation> note;
}
