package vn.ehealth.emr.dto.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createIdType;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.idFromRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.model.dto.KhoaDieuTri;
import vn.ehealth.emr.model.dto.VaoKhoa;
import vn.ehealth.emr.utils.JsonUtil;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

public class DichVuKyThuatHelper {

    private static Map<String, Object> makeParams(String maDv, Optional<String> patientId, Optional<String> encounterId) {
        var params = mapOf("category", new TokenParam(CodeSystemValue.LOAI_DICH_VU_KY_THUAT, maDv));
        
        patientId.ifPresent(x -> params.put("subject", ResourceType.Patient + "/" + x));
        encounterId.ifPresent(x -> params.put("encounter", ResourceType.Encounter + "/" + x));
        
        return params;
    }
    
    public static long countDichVuKT(String maDv, 
                Optional<String> patientId, 
                Optional<String> encounterId) {
        
        var params = makeParams(maDv, patientId, encounterId);
        return DaoFactory.getServiceRequestDao().count(params);
    }
        
    public static List<ServiceRequest> getDichVuKTList(String maDv, 
                                    Optional<String> patientId, 
                                    Optional<String> encounterId,
                                    Optional<Integer> start,
                                    Optional<Integer> count) {
        
        var params = makeParams(maDv, patientId, encounterId);        
        var lst = DaoFactory.getServiceRequestDao().search(params);
        lst = FPUtil.filter(lst, x -> x instanceof ServiceRequest);
        return transform(lst, x -> (ServiceRequest) x);
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
    
    @SuppressWarnings("unchecked")
    public static ServiceRequest saveDichVuKT(DichVuKyThuat dto) {
        if(dto == null) return null;
        
        if(dto.encounterId != null && dto.patientId == null) {
            var enc = DaoFactory.getEncounterDao().read(createIdType(dto.encounterId));
            dto.patientId = enc != null? idFromRef(enc.getSubject()) : null;
        }
        
        var entities = dto.toFhir();
        if(entities != null) {
            var serviceRequest = (ServiceRequest) entities.get("serviceRequest");
            var procedure = (Procedure) entities.get("procedure");                
            var diagnosticReport = (DiagnosticReport) entities.get("diagnosticReport");
            var specimen = (Specimen) entities.get("specimen");
            var observations = (List<Observation>) entities.get("observations");
            if(observations == null) observations = new ArrayList<>();
                    
            serviceRequest = saveServiceRequest(serviceRequest);
            if(serviceRequest != null) {
                var ref = createReference(serviceRequest);
                if(procedure != null) procedure.setBasedOn(listOf(ref));
                if(diagnosticReport != null) diagnosticReport.setBasedOn(listOf(ref));
                if(specimen != null) specimen.setRequest(listOf(ref));
                observations.forEach(x -> x.setBasedOn(listOf(ref)));
            }
            
            diagnosticReport = saveDiagnosticReport(diagnosticReport);
            if(diagnosticReport != null) {
                if(procedure != null) procedure.setReport(listOf(createReference(diagnosticReport)));                
            }
            
            procedure = saveProcedure(procedure);
            if(procedure != null) {                
                var ref = createReference(procedure);
                observations.forEach(x -> x.setPartOf(listOf(ref)));
            }
            
            specimen = saveSpecimen(specimen);
            
            if(serviceRequest != null) {
                var observationDao = DaoFactory.getObservationDao();
                var params = mapOf("basedOn", ResourceType.ServiceRequest + "/" + serviceRequest.getId());
                var oldObservations = observationDao.search(params);
                oldObservations.forEach(x -> observationDao.remove(x.getIdElement()));
                observations.forEach(x -> observationDao.create(x));
            }
                                    
            return serviceRequest;
        }
        throw new RuntimeException("No data found for DichVuKyThuat with id:" + dto.id);
    }
    
    private static KhoaDieuTri getKhoaDieuTri(@Nonnull DichVuKyThuat dto) {
        var enc = DaoFactory.getEncounterDao().read(createIdType(dto.encounterId));
        if(enc != null) {
            var falculty = DaoFactory.getOrganizationDao().readRef(enc.getServiceProvider());
            return new KhoaDieuTri(falculty);
        }
        return null;
    }
    
    public static Map<String, Object> convertDichVuKyThuatToRaw(DichVuKyThuat dto, 
                                    Optional<Boolean> includeServiceProvider, 
                                    Optional<Boolean> includeEncounter) {
        
        if(dto == null) return null;
        
        var item = JsonUtil.objectToMap(dto);
        
        if(includeServiceProvider.orElse(false)) {
            var khoaDieuTri = getKhoaDieuTri(dto);
            item.put("khoaDieuTri", khoaDieuTri);
        }
        
        if(includeEncounter.orElse(false)) {
            var encounter = DaoFactory.getEncounterDao().read(createIdType(dto.encounterId));
            if(encounter != null) {
                var vaoKhoa = VaoKhoa.fromFhir(encounter);
                item.put("vaoKhoa", vaoKhoa);
            }
        }
        
        return item;
    }
}
