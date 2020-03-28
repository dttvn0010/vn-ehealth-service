package vn.ehealth.emr.dto.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdType;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.BaseRef;
import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;
import static vn.ehealth.hl7.fhir.dao.util.DatabaseUtil.*;

public class DichVuKyThuatHelper {

    public static long countDichVuKT(String maDv, 
                Optional<String> patientId, 
                Optional<String> encounterId) {
        
        var params = mapOf("category", new TokenParam(CodeSystemValue.LOAI_DICH_VU_KY_THUAT, maDv));
        
        patientId.ifPresent(x -> params.put("subject", ResourceType.Patient + "/" + x));
        encounterId.ifPresent(x -> params.put("encounter", ResourceType.Encounter + "/" + x));
        return DaoFactory.getServiceRequestDao().count(params);
    }
        
    public static List<Procedure> getDichVuKTList(String maDv, 
                                    Optional<String> patientId, 
                                    Optional<String> encounterId,
                                    Optional<Boolean> includePatient,
                                    Optional<Boolean> includeEncounter,
                                    Optional<Boolean> includeServiceProvider,
                                    Optional<Integer> start,
                                    Optional<Integer> count) {
        
        var params = mapOf("category", new TokenParam(CodeSystemValue.LOAI_DICH_VU_KY_THUAT, maDv));
        
        patientId.ifPresent(x -> params.put("subject", ResourceType.Patient + "/" + x));
        encounterId.ifPresent(x -> params.put("encounter", ResourceType.Encounter + "/" + x));
        
        var includes = new HashSet<Include>();
        
        includes.add(new Include("Procedure:basedOn"));
        includes.add(new Include("Procedure:report"));
        
        if(includePatient.orElse(false)) {
            includes.add(new Include("Procedure:subject"));
        }
        
        //includes.add(new Include("Procedure:asserter"));
        //includes.add(new Include("Procedure:performer:actor"));
        
        if(includeEncounter.orElse(false)) {
        	includes.add(new Include("Procedure:encounter"));
        }
        
        if(includeServiceProvider.orElse(false)) {
        	includes.add(new Include("Procedure:encounter:serviceProvider"));
        }
        
        params.put("includes", includes);
        
        var lst = DaoFactory.getProcedureDao().search(params);
        lst = FPUtil.filter(lst, x -> x instanceof Procedure);
        return transform(lst, x -> (Procedure) x);
    }
    
    private static DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {        
        if(obj != null) {
            var diagnosticReportDao = DaoFactory.getDiagnosticReportDao();
            if(obj.hasId()) {
                return diagnosticReportDao.update(obj, obj.getIdElement());
            }else {
                return diagnosticReportDao.create(obj);
            }
        }
        return null;
    }
    
    private static ServiceRequest saveServiceRequest(ServiceRequest obj) {
        if(obj != null) {
            var serviceRequestDao = DaoFactory.getServiceRequestDao();
            if(obj.hasId()) {
                return serviceRequestDao.update(obj, obj.getIdElement());
            }else {
                return serviceRequestDao.create(obj);
            }
        }
        return null;
    }
    
    private static Procedure saveProcedure(Procedure obj) {
        if(obj != null) {
            var procedureDao = DaoFactory.getProcedureDao();
            if(obj.hasId()) {
                return procedureDao.update(obj, obj.getIdElement());
            }else {
                return procedureDao.create(obj);
            }
        }
        return null;
    }
    
    private static Specimen saveSpecimen(Specimen obj) {
        if((obj != null)) {
            var specimentDao = DaoFactory.getSpecimenDao();
            if(obj.hasId()) {
                return specimentDao.update(obj, obj.getIdElement());
            }else {
                return specimentDao.create(obj);
            }
        }
        return null;
    }
    
    public static void removeOldProcedureData(String procedureId) {
    	ServiceRequest serviceRequest = null;
    	DiagnosticReport diagnosticReport = null;
    	Specimen specimen = null;
    	List<Resource> observations = new ArrayList<>();
    	
    	var procedure = DaoFactory.getProcedureDao().read(createIdType(procedureId));
    	if(procedure != null) {
        	
        	if(procedure.hasReport()) {
        		var reportId = FhirUtil.idFromRef(procedure.getReportFirstRep());
        		diagnosticReport = DaoFactory.getDiagnosticReportDao().read(createIdType(reportId));
        	}
        	
        	if(procedure.hasBasedOn()) {
        		var serviceRequestId = FhirUtil.idFromRef(procedure.getBasedOnFirstRep());
        		serviceRequest = DaoFactory.getServiceRequestDao().read(createIdType(serviceRequestId));
        		
        		if(serviceRequest != null) {
        			var params = mapOf("request", ResourceType.ServiceRequest + "/" + serviceRequest.getId());        		
            		specimen = (Specimen) DaoFactory.getSpecimenDao().searchOne(params);
            		
            		params = mapOf("basedOn", ResourceType.ServiceRequest + "/" + serviceRequest.getId());
    	            observations =  DaoFactory.getObservationDao().search(params);
        		}        		
        	}
    	}
    	
    	// Delete all dependency
    	if(diagnosticReport != null) {
    		DaoFactory.getDiagnosticReportDao().remove(diagnosticReport.getIdElement());
    	}
    	
    	if(serviceRequest != null) {
    		DaoFactory.getServiceRequestDao().remove(serviceRequest.getIdElement());
    	}
    	
    	if(specimen != null) {
    		DaoFactory.getSpecimenDao().remove(specimen.getIdElement());
    	}
    	
    	if(observations != null) {
    		observations.forEach(x -> DaoFactory.getObservationDao().remove(x.getIdElement()));
    	}
    }
        
    public static void updateReferenceResource(Procedure obj, 
                            Optional<Boolean> includePatient,
                            Optional<Boolean> includeEncounter,
                            Optional<Boolean> includeServiceProvider) {
     
        if(obj == null) return;
        
        if(includePatient.orElse(false)) {
            setReferenceResource(obj.getSubject());
        }
        
        if(obj.hasPerformer()) {
            obj.getPerformer().forEach(x -> setReferenceResource(x.getActor()));
        }
        
        setReferenceResource(obj.getRecorder());
        setReferenceResource(obj.getAsserter());
        
        if(obj.hasBasedOn()) {
            setReferenceResource(obj.getBasedOnFirstRep());
            var serviceRequest = (ServiceRequest) obj.getBasedOnFirstRep().getResource();
            if(serviceRequest != null) {
                setReferenceResource(serviceRequest.getRequester());
            }
        }
        
        if(obj.hasReport()) {
            setReferenceResource(obj.getReportFirstRep());
            var diagnosticReport = (DiagnosticReport) obj.getReportFirstRep().getResource();
            if(diagnosticReport != null) {
                setReferenceResource(diagnosticReport.getPerformer());
                setReferenceResource(diagnosticReport.getResultsInterpreter());
            }
        }
        
        if(includeEncounter.orElse(false)) {
            setReferenceResource(obj.getEncounter());
            var enc = (Encounter) (obj.getEncounter().getResource());
            
            if(includeServiceProvider.orElse(false) && enc != null) {                   
                setReferenceResource(enc.getServiceProvider());
            }
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
    public static Procedure saveDichVuKT(DichVuKyThuat dto) {
        if(dto == null || dto.encounter == null) return null;
        var enc = DaoFactory.getEncounterDao().read(createIdType(dto.encounter.id));
        if(enc == null) return null;
        
        dto.patient = new BaseRef(enc.getSubject());
        
        var entities = dto.toFhir();
        if(entities != null) {
        	if(dto.id != null) {
        	    removeOldProcedureData(dto.id);
        	}
        	
            var serviceRequest = (ServiceRequest) entities.get("serviceRequest");
            var procedure = (Procedure) entities.get("procedure");                
            var diagnosticReport = (DiagnosticReport) entities.get("diagnosticReport");
            var specimen = (Specimen) entities.get("specimen");
            var observations = (List<Observation>) entities.get("observations");
            if(observations == null) observations = new ArrayList<>();
            
            // Service Request
            serviceRequest = saveServiceRequest(serviceRequest);
            if(serviceRequest != null) {
                var ref = createReference(serviceRequest);
                if(procedure != null) procedure.setBasedOn(listOf(ref));
                if(diagnosticReport != null) diagnosticReport.setBasedOn(listOf(ref));
                if(specimen != null) specimen.setRequest(listOf(ref));
                observations.forEach(x -> x.setBasedOn(listOf(ref)));
            }
            
            // DiagnosticReport
            diagnosticReport = saveDiagnosticReport(diagnosticReport);
            if(diagnosticReport != null) {
                if(procedure != null) procedure.setReport(listOf(createReference(diagnosticReport)));                
            }
            
            // Procedure
            procedure = saveProcedure(procedure);
            if(procedure != null) {                
                var ref = createReference(procedure);
                observations.forEach(x -> x.setPartOf(listOf(ref)));
            }
            
            // Specimen            
            specimen = saveSpecimen(specimen);
            if(specimen != null) {
            	serviceRequest.setSpecimen(listOf(createReference(specimen)));
            	serviceRequest = saveServiceRequest(serviceRequest);
            }
            
            // Observation
            if(serviceRequest != null) {
                var observationDao = DaoFactory.getObservationDao();
                var params = mapOf("basedOn", ResourceType.ServiceRequest + "/" + serviceRequest.getId());
                var oldObservations = observationDao.search(params);
                oldObservations.forEach(x -> observationDao.remove(x.getIdElement()));
                observations.forEach(x -> observationDao.create(x));
            }
             
            // Update result
            if(procedure != null) {
                var optFalse = Optional.of(false);
            	updateReferenceResource(procedure, optFalse, optFalse, optFalse);
            }
            
            return procedure;
        }
        throw new RuntimeException("No data found for DichVuKyThuat with id:" + dto.id);
    }
    
    public static Specimen getSpecimen(ServiceRequest serviceRequest) {
        if(serviceRequest == null) return null;
        
        var params = mapOf("request", ResourceType.ServiceRequest + "/" + serviceRequest.getId());              
        return (Specimen) DaoFactory.getSpecimenDao().searchOne(params);
    }
    
    public static List<Observation> getObservations(ServiceRequest serviceRequest) {
        if(serviceRequest == null) return new ArrayList<>();
        
        var params = mapOf("basedOn", ResourceType.ServiceRequest + "/" + serviceRequest.getId());
        var lst =  DaoFactory.getObservationDao().search(params);
        lst = FPUtil.filter(lst, x -> x instanceof Observation);
        return transform(lst, x -> (Observation) x);
    }
}
