package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "medication")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class MedicationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public BaseCodeableConcept code;
    public String status;
    //public Boolean isBrand;
    //public Boolean isOverTheCounter;
    public BaseReference manufacturer;
    public BaseCodeableConcept form;
    public MedicationBatchEntity batch;
    
    public List<MedicationIngredientEntity> ingredient;
    //public MedicationPackageEntity package_;
    //public List<BaseAttachment> image;
}
