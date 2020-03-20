package vn.ehealth.hl7.fhir.diagnostic.entity;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestPriority;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import vn.ehealth.hl7.fhir.core.entity.BaseAnnotation;
import vn.ehealth.hl7.fhir.core.entity.BaseCodeableConcept;
import vn.ehealth.hl7.fhir.core.entity.BaseIdentifier;
import vn.ehealth.hl7.fhir.core.entity.BaseReference;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

@Document(collection = "serviceRequest")
@CompoundIndex(def = "{'fhirId':1,'active':1,'version':1, 'basedOn.reference':1, 'subject.reference':1, 'encounter.reference':1}", name = "index_by_default")
public class ServiceRequestEntity extends BaseResource {
    @Id
    public ObjectId id;
    public List<BaseIdentifier> identifier;
    //List<BaseReference> definition;
    public List<BaseReference> basedOn;
    public List<BaseReference> replaces;
    public BaseIdentifier requisition;
    public String status;
    public String intent;
    public String priority;
    public Boolean doNotPerform;
    public List<BaseCodeableConcept> category;
    public BaseCodeableConcept code;
    public List<BaseCodeableConcept> orderDetail;
    @JsonIgnore public Type quantity;
    public BaseReference subject;    
    public BaseReference encounter;
    public Type occurrence;
    public Type asNeeded;
    public Date authoredOn;
    public BaseReference requester;
    public BaseCodeableConcept performerType;
    public List<BaseReference> performer;
    public List<BaseCodeableConcept> reasonCode;
    public List<BaseReference> reasonReference;
    public List<BaseReference> supportingInfo;
    public List<BaseReference> specimen;
    public List<BaseCodeableConcept> bodySite;
    public List<BaseAnnotation> note;
    public List<BaseReference> relevantHistory;
     
    
    
    public static ServiceRequestEntity fromServiceRequest(ServiceRequest obj) {
        if(obj == null) return null;
        
        var ent = new ServiceRequestEntity();
        ent.identifier = obj.hasIdentifier()? BaseIdentifier.fromIdentifierList(obj.getIdentifier()) : null;
        ent.basedOn = obj.hasBasedOn()? BaseReference.fromReferenceList(obj.getBasedOn()) : null;
        ent.replaces = obj.hasReplaces()? BaseReference.fromReferenceList(obj.getReplaces()): null;
        ent.requisition = obj.hasRequisition()? BaseIdentifier.fromIdentifier(obj.getRequisition()) : null;
        ent.status = obj.hasStatus()? obj.getStatus().toCode() : null;
        ent.intent = obj.hasIntent()? obj.getIntent().toCode() : null;
        ent.priority = obj.hasPriority()? obj.getPriority().toCode() : null;
        ent.doNotPerform = obj.hasDoNotPerform()? obj.getDoNotPerform(): null;
        ent.category = obj.hasCategory()? BaseCodeableConcept.fromCodeableConcept(obj.getCategory()) : null;
        ent.code = obj.hasCode()? BaseCodeableConcept.fromCodeableConcept(obj.getCode()) : null;
        ent.orderDetail = obj.hasOrderDetail()? BaseCodeableConcept.fromCodeableConcept(obj.getOrderDetail()) : null;
        ent.quantity = obj.hasQuantity()? obj.getQuantity() : null;
        ent.subject = obj.hasSubject()? BaseReference.fromReference(obj.getSubject()) : null;
        ent.encounter = obj.hasEncounter()? BaseReference.fromReference(obj.getEncounter()) : null;
        ent.occurrence = obj.getOccurrence();        
        ent.asNeeded = obj.getAsNeeded();
        ent.authoredOn = obj.hasAuthoredOn()? obj.getAuthoredOn() : null;
        ent.requester = obj.hasRequester()? BaseReference.fromReference(obj.getRequester()) : null;
        ent.performerType = obj.hasPerformerType()? BaseCodeableConcept.fromCodeableConcept(obj.getPerformerType()) : null;
        ent.performer = obj.hasPerformer()? BaseReference.fromReferenceList(obj.getPerformer()) : null;
        ent.reasonCode = obj.hasReasonCode()? BaseCodeableConcept.fromCodeableConcept(obj.getReasonCode()) : null;
        ent.supportingInfo = obj.hasSupportingInfo()? BaseReference.fromReferenceList(obj.getSupportingInfo()) : null;
        ent.specimen = obj.hasSpecimen()? BaseReference.fromReferenceList(obj.getSpecimen()) : null;
        ent.bodySite = obj.hasBodySite()? BaseCodeableConcept.fromCodeableConcept(obj.getBodySite()) : null;
        ent.note = obj.hasNote()? BaseAnnotation.fromAnnotationList(obj.getNote()) : null;
        ent.relevantHistory = obj.hasRelevantHistory()? BaseReference.fromReferenceList(obj.getReasonReference()) : null;
        
        return ent;
    }
    
    public static ServiceRequest toServiceRequest(ServiceRequestEntity ent) {
        if(ent == null) return null;
        
        var obj = new ServiceRequest();
        
        obj.setIdentifier(BaseIdentifier.toIdentifierList(ent.identifier));
        obj.setBasedOn(BaseReference.toReferenceList(ent.basedOn));
        obj.setReplaces(BaseReference.toReferenceList(ent.replaces));
        obj.setRequisition(BaseIdentifier.toIdentifier(ent.requisition));
        obj.setStatus(ServiceRequestStatus.fromCode(ent.status));
        obj.setIntent(ServiceRequestIntent.fromCode(ent.intent));
        obj.setPriority(ServiceRequestPriority.fromCode(ent.priority));
        if(ent.doNotPerform != null) obj.setDoNotPerform(ent.doNotPerform);
        obj.setCategory(BaseCodeableConcept.toCodeableConcept(ent.category));
        obj.setCode(BaseCodeableConcept.toCodeableConcept(ent.code));
        obj.setOrderDetail(BaseCodeableConcept.toCodeableConcept(ent.orderDetail));
        obj.setQuantity(ent.quantity);
        obj.setSubject(BaseReference.toReference(ent.subject));
        obj.setEncounter(BaseReference.toReference(ent.encounter));
        obj.setOccurrence(ent.occurrence);
        obj.setAsNeeded(ent.asNeeded);
        obj.setAuthoredOn(ent.authoredOn);
        obj.setRequester(BaseReference.toReference(ent.requester));
        obj.setPerformerType(BaseCodeableConcept.toCodeableConcept(ent.performerType));
        obj.setPerformer(BaseReference.toReferenceList(ent.performer));
        obj.setReasonCode(BaseCodeableConcept.toCodeableConcept(ent.reasonCode));
        obj.setSupportingInfo(BaseReference.toReferenceList(ent.supportingInfo));
        obj.setSpecimen(BaseReference.toReferenceList(ent.specimen));
        obj.setBodySite(BaseCodeableConcept.toCodeableConcept(ent.bodySite));
        obj.setNote(BaseAnnotation.toAnnotationList(ent.note));
        obj.setRelevantHistory(BaseReference.toReferenceList(ent.relevantHistory));
        return obj;
        
    }
    
    public String getOrderDetail() {
        String text = "";
        if(orderDetail != null && orderDetail.size() > 0) {
            text = orderDetail.get(0).text;
        }
        return text;
    }
}
