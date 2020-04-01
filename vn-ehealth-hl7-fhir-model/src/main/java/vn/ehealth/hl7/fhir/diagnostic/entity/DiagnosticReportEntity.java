package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import vn.ehealth.hl7.fhir.core.entity.BaseAttachment;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "diagnosticReport")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class DiagnosticReportEntity extends BaseResource {

    public class DiagnosticReportMedia {

        public String comment;
        public BaseReference link;
    }
    
    @Id
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier;
    public List<BaseReference> basedOn;
    public String status;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public BaseReference subject;
    public BaseReference encounter;
    public BaseType effective;
    public Date issued;
    public List<BaseReference> performer;    
    public List<BaseReference> resultsInterpreter;
    public List<BaseReference> specimen;
    public List<BaseReference> result;
    public List<BaseReference> imagingStudy;
    public List<DiagnosticReportMedia> media;
    public String conclusion;
    public List<BaseCodeableConcept> conclusionCode;
    public List<BaseAttachment> presentedForm;
}
