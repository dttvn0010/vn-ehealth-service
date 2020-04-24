package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseBackboneElement;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "organization")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class OrganizationEntity extends BaseResource {
    
    public static class OrganizationContact extends BaseBackboneElement {

        public BaseCodeableConcept purpose;
        public BaseHumanName name;
        public List<BaseContactPoint> telecom;
        public BaseAddress address;    
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public Boolean active;
    public List<BaseIdentifier> identifier;
    public List<BaseCodeableConcept> type;
    public String name;
    public List<String> alias;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public BaseReference partOf;
    public List<OrganizationContact> contact;
    public List<BaseReference> endpoint;
}
