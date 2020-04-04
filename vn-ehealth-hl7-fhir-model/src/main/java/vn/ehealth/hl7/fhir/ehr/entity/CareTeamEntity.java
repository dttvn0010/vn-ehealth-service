package vn.ehealth.hl7.fhir.ehr.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "careTeam")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class CareTeamEntity extends BaseResource{
    
    public static class CareTeamParticipant {
        public List<BaseCodeableConcept> role;
        public BaseReference member;
        public BaseReference onBehalfOf;
        public BasePeriod period;
    }
    
    @Id
    @Indexed(name = "_id_")
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    public List<BaseCodeableConcept> category;
    public String name;
    public BaseReference subject;
    public BaseReference encounter;
    public BasePeriod period;
    public List<CareTeamParticipant> participant;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> managingOrganization;
    public List<BaseContactPoint> telecom;
    public List<BaseAnnotation> note;
}
