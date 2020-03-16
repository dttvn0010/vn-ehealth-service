package vn.ehealth.hl7.fhir.clinical.entity;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BasePeriod;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "carePlan")
@CompoundIndex(def = "{'fhir_id':1,'active':1,'version':1}", name = "index_by_default")
public class CarePlanEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //public List<BaseReference> definition;
    public List<BaseReference> basedOn;
    public List<BaseReference> replaces;
    public List<BaseReference> partOf;
    public String status;
    public String intent;
    public List<BaseCodeableConcept> category;
    public String title;
    public String description;
    public BaseReference subject;
    public BaseReference encounter;
    public BasePeriod period;
    public BaseReference author;
    public List<BaseReference> careTeam;
    public List<BaseReference> addresses;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> goal;
    public List<CarePlanActivityEntity> activity;
    public List<BaseAnnotation> note;
    
    
    public static CarePlanEntity fromCarePlan(CarePlan obj) {
        if(obj == null) return null;
        
        var ent = new CarePlanEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.basedOn = BaseReference.fromReferenceList(obj.getBasedOn());
        ent.replaces = BaseReference.fromReferenceList(obj.getReplaces());
        ent.partOf = BaseReference.fromReferenceList(obj.getPartOf());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.intent = Optional.ofNullable(obj.getIntent()).map(x -> x.toCode()).orElse(null);
        ent.category = BaseCodeableConcept.fromCodeableConcept(obj.getCategory());
        ent.title = obj.getTitle();
        ent.description = obj.getDescription();
        ent.subject = BaseReference.fromReference(obj.getSubject());
        ent.encounter = BaseReference.fromReference(obj.getEncounter());
        ent.period = BasePeriod.fromPeriod(obj.getPeriod());
        ent.author =  BaseReference.fromReference(obj.getAuthor());
        ent.careTeam = BaseReference.fromReferenceList(obj.getCareTeam());
        ent.addresses = BaseReference.fromReferenceList(obj.getAddresses());
        ent.supportingInfo = BaseReference.fromReferenceList(obj.getSupportingInfo());
        ent.goal = BaseReference.fromReferenceList(obj.getGoal());
        ent.activity = transform(obj.getActivity(), CarePlanActivityEntity::fromCarePlanActivityComponent);
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        return ent;        
    }
    
    
    public static CarePlan toCarePlan(CarePlanEntity ent) {
        if(ent == null) return null;
        
        var obj = new CarePlan();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setReplaces(BaseReference.toReferenceList(ent.replaces));
        obj.setPartOf(BaseReference.toReferenceList(ent.partOf));
        obj.setStatus(CarePlanStatus.fromCode(ent.status));
        obj.setIntent(CarePlanIntent.fromCode(ent.intent));
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setTitle(ent.title);
        obj.setDescription(ent.description);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setPeriod(BasePeriod.toPeriod(ent.period));
        obj.setAuthor(BaseReference.toReference(ent.author));
        obj.setCareTeam(BaseReference.toReferenceList(ent.careTeam));
        obj.setAddresses(BaseReference.toReferenceList(ent.addresses));
        obj.setSupportingInfo(BaseReference.toReferenceList(ent.supportingInfo));
        obj.setGoal(BaseReference.toReferenceList(ent.goal));
        obj.setActivity(transform(ent.activity, CarePlanActivityEntity::toCarePlanActivityComponent));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        
        
        return obj;
    }
}
