package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.Date;


import java.util.List;
import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "clinicalImpression")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ClinicalImpressionEntity extends BaseResource {

    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public BaseCodeableConcept code;
    public String description;
    public BaseReference subject;
    public BaseReference encounter;
    public Type effective;
    public Date date;
    public BaseReference assessor;
    public BaseReference previous;
    public List<BaseReference> problem;
    public List<ClinicalInvestigationEntity> investigation;
    public List<String> protocol;
    public String summary;
    public List<ClinicalFindingEntity> finding;
    public List<BaseCodeableConcept> prognosisCodeableConcept;
    public List<BaseReference> prognosisReference;
    public List<BaseAnnotation> note;
}
