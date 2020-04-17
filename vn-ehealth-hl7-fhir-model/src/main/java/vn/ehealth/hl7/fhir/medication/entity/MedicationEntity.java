package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseRatio;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import vn.ehealth.hl7.fhir.core.entity.BaseType;


@Document(collection = "medication")
@CompoundIndex(def = "{'_fhirId':1,'_active':1,'_version':1}", name = "index_by_default")
public class MedicationEntity extends BaseResource {
    
    public static class MedicationBatch {

        public Date expirationDate;
        public String lotNumber;
    }

    public static class MedicationIngredient{
        public BaseType item;
        public boolean isActive;
        public BaseRatio strength;
    }
    
    @Id
    @Indexed(name = "_id_")
    @JsonIgnore public ObjectId id;
    public List<BaseIdentifier> identifier; 
    public BaseCodeableConcept code;
    public String status;
    //public Boolean isBrand;
    //public Boolean isOverTheCounter;
    public BaseReference manufacturer;
    public BaseCodeableConcept form;
    public BaseRatio amount;
    public MedicationBatch batch;
    
    public List<MedicationIngredient> ingredient;
    //public MedicationPackageEntity package_;
    //public List<BaseAttachment> image;
}
