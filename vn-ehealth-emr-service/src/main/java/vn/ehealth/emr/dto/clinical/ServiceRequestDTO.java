package vn.ehealth.emr.dto.clinical;

import java.util.Date;

import org.hl7.fhir.r4.model.ServiceRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.dto.BaseDTO;
import vn.ehealth.emr.dto.CodeableConceptDTO;
import vn.ehealth.emr.dto.ReferenceDTO;

@JsonInclude(Include.NON_NULL)
public class ServiceRequestDTO extends BaseDTO{
    
    public ReferenceDTO patient;
    public ReferenceDTO encounter;
    public CodeableConceptDTO category;
    public CodeableConceptDTO code;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date authoredOn;
    
    public ReferenceDTO requester;
    public CodeableConceptDTO orderDetail;
        
    public static ServiceRequestDTO fromFhir(ServiceRequest obj) {
        if(obj == null) return null;
        
        var dto = new ServiceRequestDTO();
        dto.id = obj.getId();
        dto.patient = ReferenceDTO.fromFhir(obj.getSubject());
        dto.encounter = ReferenceDTO.fromFhir(obj.getEncounter());
        dto.category = CodeableConceptDTO.fromFhir(obj.getCategoryFirstRep());
        dto.code = CodeableConceptDTO.fromFhir(obj.getCode());
        dto.authoredOn = obj.getAuthoredOn();
        dto.requester = ReferenceDTO.fromFhir(obj.getRequester());
        dto.orderDetail = CodeableConceptDTO.fromFhir(obj.getOrderDetailFirstRep());
        
        return dto;
    }
}
