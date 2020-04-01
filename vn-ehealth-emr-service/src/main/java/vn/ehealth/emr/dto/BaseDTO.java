package vn.ehealth.emr.dto;

import java.util.HashMap;
import java.util.Map;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;

import vn.ehealth.emr.dto.clinical.ProcedureDTO;
import vn.ehealth.emr.dto.clinical.ServiceRequestDTO;
import vn.ehealth.emr.dto.diagnostic.DiagnosticReportDTO;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.emr.dto.diagnostic.SpecimenDTO;
import vn.ehealth.emr.dto.ehr.EncounterDTO;
import vn.ehealth.emr.dto.patient.PatientDTO;
import vn.ehealth.emr.dto.provider.OrganizationDTO;
import vn.ehealth.emr.dto.provider.PractitionerDTO;

abstract public class BaseDTO {
    public String id;
    
    public Map<String, Object> computes = new HashMap<>(); 
    
    public static BaseDTO fromResource(DomainResource obj) {
        if(obj == null) return null;
        
        if(obj instanceof Patient) 
            return PatientDTO.fromFhir((Patient) obj);
        
        if(obj instanceof Encounter) 
            return EncounterDTO.fromFhir((Encounter) obj);
        
        if(obj instanceof Procedure) 
            return ProcedureDTO.fromFhir((Procedure) obj);
        
        if(obj instanceof ServiceRequest) 
            return ServiceRequestDTO.fromFhir((ServiceRequest) obj);
        
        if(obj instanceof DiagnosticReport) 
            return DiagnosticReportDTO.fromFhir((DiagnosticReport) obj);
        
        if(obj instanceof Observation) 
            return ObservationDTO.fromFhir((Observation) obj);
        
        if(obj instanceof Specimen) 
            return SpecimenDTO.fromFhir((Specimen) obj);
        
        if(obj instanceof Organization)
            return OrganizationDTO.fromFhir((Organization) obj);
        
        if(obj instanceof Practitioner) {
            return PractitionerDTO.fromFhir((Practitioner) obj);
        }
        throw new RuntimeException("Unsupported resource type :" + obj.getResourceType());
    }
}
