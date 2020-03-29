package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseRatio;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;


@Document(collection = "medication")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationEntity extends BaseResource {
    
    public static class MedicationBatch {

        public Date expirationDate;
        public String lotNumber;
    }

    public static class MedicationIngredient{
        @JsonIgnore public Object item;
        public boolean isActive;
        public BaseRatio strength;
    }
    
    @Id
    public ObjectId id;
    public BaseCodeableConcept code;
    public String status;
    //public Boolean isBrand;
    //public Boolean isOverTheCounter;
    public BaseReference manufacturer;
    public BaseCodeableConcept form;
    public MedicationBatch batch;
    
    public List<MedicationIngredient> ingredient;
    //public MedicationPackageEntity package_;
    //public List<BaseAttachment> image;
}
