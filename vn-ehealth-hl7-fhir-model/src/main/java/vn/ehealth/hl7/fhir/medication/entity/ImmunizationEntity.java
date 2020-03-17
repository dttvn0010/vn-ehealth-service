package vn.ehealth.hl7.fhir.medication.entity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Immunization.ImmunizationStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;



import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseQuantity;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;

@Document(collection = "immunization")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1}", name = "index_by_default")
public class ImmunizationEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    public String status;
    //public Boolean notGiven;
    public BaseCodeableConcept vaccineCode;
    public BaseReference patient;
    public BaseReference encounter;
    public Date recorded;
    public boolean primarySource;
    public BaseCodeableConcept reportOrigin;
    public BaseReference location;
    public BaseReference manufacturer;
    public String lotNumber;
    public Date expirationDate;
    public BaseCodeableConcept site;
    public BaseCodeableConcept route;
    public BaseQuantity doseQuantity;
    public List<ImmunizationPerformerEntity> performer;
    public List<BaseAnnotation> note;
    // public ImmunizationExplanationEntity explanation;
    public List<ImmunizationReactionEntity> reaction;
    // public List<ImmunizationVaccinationProtocolEntity> vaccinationProtocol;
    
    public static ImmunizationEntity fromImmunization(Immunization obj) {
        if(obj == null) return null;
        var ent = new ImmunizationEntity();
        ent.identifier = BaseIdentifier.fromIdentifierList(obj.getIdentifier());
        ent.status = Optional.ofNullable(obj.getStatus()).map(x -> x.toCode()).orElse(null);
        ent.vaccineCode = BaseCodeableConcept.fromCodeableConcept(obj.getVaccineCode());
        ent.patient = BaseReference.fromReference(obj.getPatient());
        ent.encounter = BaseReference.fromReference(obj.getEncounter());
        ent.recorded = obj.getRecorded();        
        ent.primarySource = obj.getPrimarySource();
        ent.reportOrigin = BaseCodeableConcept.fromCodeableConcept(obj.getReportOrigin());
        ent.location = BaseReference.fromReference(obj.getLocation());
        ent.manufacturer = BaseReference.fromReference(obj.getManufacturer());
        ent.lotNumber = obj.getLotNumber();
        ent.expirationDate = obj.getExpirationDate();
        ent.site = BaseCodeableConcept.fromCodeableConcept(obj.getSite());
        ent.route = BaseCodeableConcept.fromCodeableConcept(obj.getRoute());
        ent.doseQuantity = BaseQuantity.fromQuantity(obj.getDoseQuantity());
        ent.performer = transform(obj.getPerformer(), ImmunizationPerformerEntity::fromImmunizationPerformerComponent);
        ent.note = BaseAnnotation.fromAnnotationList(obj.getNote());
        ent.reaction = transform(obj.getReaction(), ImmunizationReactionEntity::fromImmunizationReactionComponent);
        return ent;
    }
    
    public static Immunization toImmunization(ImmunizationEntity ent) {
        if(ent == null) return null;
        var obj = new Immunization();
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setStatus(ImmunizationStatus.fromCode(ent.status));
        obj.setVaccineCode(BaseCodeableConcept.toCodeableConcept(ent.vaccineCode));
        obj.setPatient(BaseReference.toReference(ent.patient));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setRecorded(ent.recorded);
        obj.setPrimarySource(ent.primarySource);
        obj.setReportOrigin(BaseCodeableConcept.toCodeableConcept(ent.reportOrigin));
        obj.setLocation(BaseReference.toReference(ent.location));
        obj.setManufacturer(BaseReference.toReference(ent.manufacturer));
        obj.setLotNumber(ent.lotNumber);
        obj.setExpirationDate(ent.expirationDate);
        obj.setSite(BaseCodeableConcept.toCodeableConcept(ent.site));
        obj.setRoute(BaseCodeableConcept.toCodeableConcept(ent.route));
        obj.setDoseQuantity(BaseQuantity.toQuantity(ent.doseQuantity));
        obj.setPerformer(transform(ent.performer, ImmunizationPerformerEntity::toImmunizationPerformerComponent));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setReaction(transform(ent.reaction, ImmunizationReactionEntity::toImmunizationReactionComponent));
        
        return obj;
                
        
    }
}
