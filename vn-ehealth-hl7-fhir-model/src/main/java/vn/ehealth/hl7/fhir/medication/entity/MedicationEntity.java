package vn.ehealth.hl7.fhir.medication.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Medication.MedicationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "medication")
public class MedicationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public CodeableConcept code;
    public String status;
    //public Boolean isBrand;
    //public Boolean isOverTheCounter;
    public BaseReference manufacturer;
    public CodeableConcept form;
    public MedicationBatchEntity batch;
    
    public List<MedicationIngredientEntity> ingredient;
    //public MedicationPackageEntity package_;
    //public List<BaseAttachment> image;
    
    
    public static MedicationEntity fromMedication(Medication obj) {
        if(obj == null) return null;
        var ent = new MedicationEntity();
        ent.code = obj.getCode();
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        
        ent.manufacturer =  BaseReference.fromReference(obj.getManufacturer());
        ent.form = obj.getForm();
        ent.batch = MedicationBatchEntity.fromMedicationBatchComponent(obj.getBatch());
        ent.ingredient = transform(obj.getIngredient(), MedicationIngredientEntity::fromMedicationIngredientComponent);
        return ent;
    }
    
    public static Medication toMedication(MedicationEntity ent) {
        if(ent == null) return null;
        var obj = new Medication();
        obj.setCode(ent.code);
        obj.setStatus(MedicationStatus.fromCode(ent.status));
        obj.setManufacturer(BaseReference.toReference(ent.manufacturer));
        obj.setForm(ent.form);
        obj.setBatch(MedicationBatchEntity.toMedicationBatchComponent(ent.batch));
        obj.setIngredient(transform(ent.ingredient, MedicationIngredientEntity::toMedicationIngredientComponent));
        return obj;
    }
}
