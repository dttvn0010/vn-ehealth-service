package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.view.DTOView;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@Document(collection = "organization")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class OrganizationEntity extends BaseResource {
    
    public static class OrganizationContact {

        public BaseCodeableConcept purpose;
        public BaseHumanName name;
        public List<BaseContactPoint> telecom;
        public BaseAddress address;    
    }
    
    @Id
    @JsonIgnore public ObjectId id;

    public List<BaseIdentifier> identifier;
    public List<BaseCodeableConcept> type;
    public String name;
    public List<String> alias;
    public List<BaseContactPoint> telecom;
    public List<BaseAddress> address;
    public BaseReference partOf;
    public List<OrganizationContact> contact;
    public List<BaseReference> endpoint;
    
    @JsonView(DTOView.class)
    public Map<String, Object> getDto() {
        return mapOf(
                    "name", name,
                    "address", address
                );
    }
    
    public static  Map<String, Object> toDto(OrganizationEntity ent) {
        if(ent == null) return null;
        return ent.getDto();
    }
}
