package vn.ehealth.emr.dto.controller;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.entry;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.listOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.transform;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.createReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.ChanDoanHinhAnh;
import vn.ehealth.emr.model.dto.DichVuKyThuat;
import vn.ehealth.emr.model.dto.GiaiPhauBenh;
import vn.ehealth.emr.model.dto.PhauThuatThuThuat;
import vn.ehealth.emr.model.dto.XetNghiem;
import vn.ehealth.emr.utils.Constants.CodeSystemValue;
import vn.ehealth.emr.utils.Constants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ServiceRequestDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.DiagnosticReportDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.SpecimenDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@RestController
@RequestMapping("/api/dich_vu_ky_thuat")
public class DichVuKyThuatController {

private static Logger logger = LoggerFactory.getLogger(DichVuKyThuatController.class);
    
    @Autowired private EncounterDao enCounterDao;
    @Autowired private ServiceRequestDao serviceRequestDao;
    @Autowired private ProcedureDao procedureDao;
    @Autowired private DiagnosticReportDao  diagnosticReportDao;
    @Autowired private SpecimenDao specimenDao;
    @Autowired private ObservationDao observationDao;
    
    private Map<String, Object> makeParams(String maDv, Optional<String> patientId, Optional<String> encounterId) {
        var params = mapOf(
            entry("category.coding.code", (Object) maDv),
            entry("category.coding.system", CodeSystemValue.LOAI_DICH_VU_KY_THUAT)
        );
        
        patientId.ifPresent(x -> params.put("subject.reference", ResourceType.Patient + "/" + x));
        encounterId.ifPresent(x -> params.put("encounter.reference", ResourceType.Encounter + "/" + x));
        
        return params;
    }
        
    private long countDichVuKT(String maDv, 
                Optional<String> patientId, 
                Optional<String> encounterId) {
        
        var params = makeParams(maDv, patientId, encounterId);
        return serviceRequestDao.count(params);
    }
        
    private List<ServiceRequest> getDichVuKTList(String maDv, 
                                    Optional<String> patientId, 
                                    Optional<String> encounterId,
                                    Optional<Integer> start,
                                    Optional<Integer> count) {
        
        var params = makeParams(maDv, patientId, encounterId);        
        return serviceRequestDao.find(params, start.orElse(-1), count.orElse(-1));
    }
    
    private DiagnosticReport saveDiagnosticReport(DiagnosticReport obj) {
        if(obj != null) {
            if(obj.hasId()) {
                return diagnosticReportDao.update(obj, obj.getIdElement());
            }else {
                return diagnosticReportDao.create(obj);
            }
        }
        return null;
    }
    
    private ServiceRequest saveServiceRequest(ServiceRequest obj) {
        if(obj != null) {
            if(obj.hasId()) {
                return serviceRequestDao.update(obj, obj.getIdElement());
            }else {
                return serviceRequestDao.create(obj);
            }
        }
        return null;
    }
    
    private Procedure saveProcedure(Procedure obj) {
        if(obj != null) {
            if(obj.hasId()) {
                return procedureDao.update(obj, obj.getIdElement());
            }else {
                return procedureDao.create(obj);
            }
        }
        return null;
    }
    
    private Specimen saveSpecimen(Specimen obj) {
        if((obj != null)) {
            if(obj.hasId()) {
                return specimenDao.update(obj, obj.getIdElement());
            }else {
                return specimenDao.create(obj);                        
            }
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private ServiceRequest saveDichVuKT(@Nonnull DichVuKyThuat dto) {
        if(dto.encounterId != null && dto.patientId == null) {
            var enc = enCounterDao.read(createIdType(dto.encounterId));
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
                var oldObservations = observationDao.getByRequest(serviceRequest.getIdElement());
                oldObservations.forEach(x -> observationDao.remove(x.getIdElement()));
                observations.forEach(x -> observationDao.create(x));
            }
                                    
            return serviceRequest;
        }
        throw new RuntimeException("No data found for DichVuKyThuat with id:" + dto.id);
    }
    
    // ========================================  ChanDoanHinhAnh ===============================
    private boolean isChanDoanHinhAnh(ServiceRequest obj) {
        if(obj != null && obj.hasCategory()) {
            for(var concept : obj.getCategory()) {
                boolean isCdha = conceptHasCode(concept, LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                                                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
                if(isCdha) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/count_cdha")
    public long countChanDoanHinhAnh(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, patientId, encounterId);
    }
    
    @GetMapping("/get_cdha_list")
    public ResponseEntity<?> getChanDoanHinhAnhList(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId,
                                                    @RequestParam Optional<Integer> start,
                                                    @RequestParam Optional<Integer> count) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, patientId, encounterId, start, count);
        var result = transform(lst, x -> new ChanDoanHinhAnh(x));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_cdha_by_id/{id}")
    public ResponseEntity<?> getChanDoanHinhAnhById(@PathVariable String id) {
        var obj = serviceRequestDao.read(new IdType(id));
        if(isChanDoanHinhAnh(obj)) {
            return ResponseEntity.ok(new ChanDoanHinhAnh(obj));
        }
        return new ResponseEntity<>("No chanDoanHinhAnh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_cdha")
    public ResponseEntity<?> saveChanDoanHinhAnh(@RequestBody ChanDoanHinhAnh dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var cdha = new ChanDoanHinhAnh(serviceRequest);
            var result = mapOf(entry("success", true), entry("chanDoanHinhAnh", cdha));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save chanDoanHinhAnh: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  PhauThuatThuThuat ===============================
    private boolean isPhauThuatThuThuat(ServiceRequest obj) {
        if(obj != null && obj.hasCategory()) {
            for(var concept : obj.getCategory()) {
                boolean isPttt = conceptHasCode(concept, LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                                                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
                if(isPttt) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/count_pttt")
    public long countPhauThuatThuThuat(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, patientId, encounterId);
    }
    
    @GetMapping("/get_pttt_list")
    public ResponseEntity<?> getPhauThuatThuThuatList(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId,
                                                    @RequestParam Optional<Integer> start,
                                                    @RequestParam Optional<Integer> end) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, patientId, encounterId, start, end);
        var result = transform(lst, x -> new PhauThuatThuThuat(x));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_pttt_by_id/{id}")
    public ResponseEntity<?> getPhauThuatThuThuatById(@PathVariable String id) {
        var obj = serviceRequestDao.read(new IdType(id));
        if(isPhauThuatThuThuat(obj)) {
            return ResponseEntity.ok(new PhauThuatThuThuat(obj));
        }
        return new ResponseEntity<>("No phauThuatThuThuat with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_pttt")
    public ResponseEntity<?> savePhauThuatThuThuat(@RequestBody PhauThuatThuThuat dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var pttt = new PhauThuatThuThuat(serviceRequest);
            var result = mapOf(entry("success", true), entry("phauThuatThuThuat", pttt));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save phauThuatThuThuat: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  GiaiPhauBenh ===============================
    private boolean isGiaiPhauBenh(ServiceRequest obj) {
        if(obj != null && obj.hasCategory()) {
            for(var concept : obj.getCategory()) {
                boolean isGpb = conceptHasCode(concept, LoaiDichVuKT.GIAI_PHAU_BENH, 
                                                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
                if(isGpb) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/count_gpb")
    public long countGiaiPhauBenh(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.GIAI_PHAU_BENH, patientId, encounterId);
    }
    
    @GetMapping("/get_gpb_list")
    public ResponseEntity<?> getGiaiPhauBenhList(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId,
                                                    @RequestParam Optional<Integer> start,
                                                    @RequestParam Optional<Integer> count) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.GIAI_PHAU_BENH, patientId, encounterId, start, count);
        var result = transform(lst, x -> new GiaiPhauBenh(x));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_gpb_by_id/{id}")
    public ResponseEntity<?> getGiaiPhauBenhById(@PathVariable String id) {
        var obj = serviceRequestDao.read(new IdType(id));
        if(isGiaiPhauBenh(obj)) {
            return ResponseEntity.ok(new GiaiPhauBenh(obj));
        }
        return new ResponseEntity<>("No giaiPhauBenh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_gpb")
    public ResponseEntity<?> saveGiaiPhauBenh(@RequestBody GiaiPhauBenh dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var gpb = new GiaiPhauBenh(serviceRequest);
            var result = mapOf(entry("success", true), entry("giaiPhauBenh", gpb));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save giaiPhauBenh: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  XetNghiem ===============================
    private boolean isXetNghiem(ServiceRequest obj) {
        if(obj != null && obj.hasCategory()) {
            for(var concept : obj.getCategory()) {
                boolean isPttt = conceptHasCode(concept, LoaiDichVuKT.XET_NGHIEM, 
                                                CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
                if(isPttt) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/count_xet_nghiem")
    public long countXetNgheim(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.XET_NGHIEM, patientId, encounterId);
    }
    
    @GetMapping("/get_xet_nghiem_list")
    public ResponseEntity<?> getXetNghiemList(@RequestParam Optional<String> patientId, 
                                                    @RequestParam Optional<String> encounterId,
                                                    @RequestParam Optional<Integer> start,
                                                    @RequestParam Optional<Integer> end) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.XET_NGHIEM, patientId, encounterId, start, end);
        var result = transform(lst, x -> new XetNghiem(x));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_xet_nghiem_by_id/{id}")
    public ResponseEntity<?> getXetNghiemById(@PathVariable String id) {
        var obj = serviceRequestDao.read(new IdType(id));
        if(isXetNghiem(obj)) {
            return ResponseEntity.ok(new XetNghiem(obj));
        }
        return new ResponseEntity<>("No xetNghiem with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_xet_nghiem")
    public ResponseEntity<?> saveXetNghiem(@RequestBody XetNghiem dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var xetNghiem = new XetNghiem(serviceRequest);
            var result = mapOf(entry("success", true), entry("xetNghiem", xetNghiem));
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save xetNghiem: ", e);
            var result = mapOf(entry("success", false), entry("error", e.getMessage()));
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
