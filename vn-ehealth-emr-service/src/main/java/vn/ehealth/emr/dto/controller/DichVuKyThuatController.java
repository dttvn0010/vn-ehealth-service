package vn.ehealth.emr.dto.controller;

import java.util.Optional;

import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.ChanDoanHinhAnh;
import vn.ehealth.emr.model.dto.GiaiPhauBenh;
import vn.ehealth.emr.model.dto.PhauThuatThuThuat;
import vn.ehealth.emr.model.dto.XetNghiem;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiDichVuKT;
import vn.ehealth.hl7.fhir.clinical.dao.impl.ProcedureDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.emr.dto.controller.DichVuKyThuatHelper.*;

@RestController
@RequestMapping("/api/dich_vu_ky_thuat")
public class DichVuKyThuatController {

private static Logger logger = LoggerFactory.getLogger(DichVuKyThuatController.class);
    
    @Autowired private ProcedureDao procedureDao;
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDichVuKyThuat(@PathVariable String id) {
    	try {
    		DichVuKyThuatHelper.removeOldProcedureData(id);
    		procedureDao.remove(createIdType(id));
    		return ResponseEntity.ok(mapOf("success", true));
    	}catch(Exception e) {
    		var result = mapOf("success", false, "error", e.getMessage());
    		return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    	}
    }
    // ========================================  ChanDoanHinhAnh ===============================
    private boolean isChanDoanHinhAnh(Procedure obj) {
        if(obj != null && obj.hasCategory()) {
            return conceptHasCode(obj.getCategory(), LoaiDichVuKT.CHAN_DOAN_HINH_ANH, 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        }
        return false;
    }
    
    @GetMapping("/count_cdha")
    public long countChanDoanHinhAnh(@RequestParam Optional<String> patientId, 
                                    @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, patientId, encounterId);
    }
    
    @GetMapping("/get_cdha_list")
    public ResponseEntity<?> getChanDoanHinhAnhList(
    							@RequestParam Optional<String> patientId, 
                                @RequestParam Optional<String> encounterId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.CHAN_DOAN_HINH_ANH, patientId, encounterId, 
                                    includePatient, includeEncounter, includeServiceProvider,
                                    start, count);
        
        var result = transform(lst, x -> new ChanDoanHinhAnh(x));
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_cdha_by_id/{id}")
    public ResponseEntity<?> getChanDoanHinhAnhById(@PathVariable String id, 
                                @RequestParam Optional<Boolean> includePatient,
    							@RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider){
        
    	var obj = procedureDao.read(new IdType(id));
        if(isChanDoanHinhAnh(obj)) {
        	updateReferenceResource(obj, includePatient, includeEncounter, includeServiceProvider);        	
        	var cdha = new ChanDoanHinhAnh(obj);        	
            return ResponseEntity.ok(cdha);
        }
        return new ResponseEntity<>("No chanDoanHinhAnh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_cdha")
    public ResponseEntity<?> saveChanDoanHinhAnh(@RequestBody ChanDoanHinhAnh dto) {
        try {
            var procedure = saveDichVuKT(dto);
            var cdha = new ChanDoanHinhAnh(procedure);            
            var result = mapOf("success", true, "chanDoanHinhAnh", cdha);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save chanDoanHinhAnh: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  PhauThuatThuThuat ===============================
    private boolean isPhauThuatThuThuat(Procedure obj) {
        if(obj != null && obj.hasCategory()) {
        	boolean isPttt = conceptHasCode(obj.getCategory(), LoaiDichVuKT.PHAU_THUAT_THU_THUAT, 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    		if(isPttt) return true;
        }
        return false;
    }
    
   
    
    @GetMapping("/count_pttt")
    public long countPhauThuatThuThuat(@RequestParam Optional<String> patientId, 
                                        @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, patientId, encounterId);
    }
    
    @GetMapping("/get_pttt_list")
    public ResponseEntity<?> getPhauThuatThuThuatList(
    							@RequestParam Optional<String> patientId, 
                                @RequestParam Optional<String> encounterId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> end) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.PHAU_THUAT_THU_THUAT, patientId, encounterId, 
        								includePatient, includeEncounter, includeServiceProvider, 
        								start, end);

        var result = transform(lst, x -> new PhauThuatThuThuat(x));
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_pttt_by_id/{id}")
    public ResponseEntity<?> getPhauThuatThuThuatById(@PathVariable String id,
                                @RequestParam Optional<Boolean> includePatient,
					    		@RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider) {
    	
        var obj = procedureDao.read(new IdType(id));
        if(isPhauThuatThuThuat(obj)) {
        	updateReferenceResource(obj, includePatient, includeEncounter, includeServiceProvider);
        	var pttt = new PhauThuatThuThuat(obj);        	
            return ResponseEntity.ok(pttt);
        }
        return new ResponseEntity<>("No phauThuatThuThuat with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_pttt")
    public ResponseEntity<?> savePhauThuatThuThuat(@RequestBody PhauThuatThuThuat dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var pttt = new PhauThuatThuThuat(serviceRequest);
            var result = mapOf("success", true, "phauThuatThuThuat", pttt);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save phauThuatThuThuat: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  GiaiPhauBenh ===============================
    private boolean isGiaiPhauBenh(Procedure obj) {
        if(obj != null && obj.hasCategory()) {
        	boolean isGpb = conceptHasCode(obj.getCategory(), LoaiDichVuKT.GIAI_PHAU_BENH, 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
    		if(isGpb) return true;
        }
        return false;
    }
    
    @GetMapping("/count_gpb")
    public long countGiaiPhauBenh(@RequestParam Optional<String> patientId, 
                                  @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.GIAI_PHAU_BENH, patientId, encounterId);
    }
    
    @GetMapping("/get_gpb_list")
    public ResponseEntity<?> getGiaiPhauBenhList(
    							@RequestParam Optional<String> patientId, 
                                @RequestParam Optional<String> encounterId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> count) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.GIAI_PHAU_BENH, patientId, encounterId,
        							includePatient, includeEncounter, includeServiceProvider,
        							start, count);
        
        var result = transform(lst, x -> new GiaiPhauBenh(x, false));
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_gpb_by_id/{id}")
    public ResponseEntity<?> getGiaiPhauBenhById(@PathVariable String id,
                                @RequestParam Optional<Boolean> includePatient,
					    		@RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider) {
    	
        var obj = procedureDao.read(new IdType(id));
        if(isGiaiPhauBenh(obj)) {
        	updateReferenceResource(obj, includePatient, includeEncounter, includeServiceProvider);
        	var gpb = new GiaiPhauBenh(obj, true);
            return ResponseEntity.ok(gpb);
        }
        return new ResponseEntity<>("No giaiPhauBenh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_gpb")
    public ResponseEntity<?> saveGiaiPhauBenh(@RequestBody GiaiPhauBenh dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var gpb = new GiaiPhauBenh(serviceRequest, true);
            var result = mapOf("success", true, "giaiPhauBenh", gpb);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save giaiPhauBenh: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================  XetNghiem ===============================
    private boolean isXetNghiem(Procedure obj) {
        if(obj != null && obj.hasCategory()) {
        	boolean isXetNghiem = conceptHasCode(obj.getCategory(), LoaiDichVuKT.XET_NGHIEM, 
                    CodeSystemValue.LOAI_DICH_VU_KY_THUAT);
        	
        	if(isXetNghiem) return true;
        }
        return false;
    }
    
    @GetMapping("/count_xet_nghiem")
    public long countXetNgheim(@RequestParam Optional<String> patientId, 
                                @RequestParam Optional<String> encounterId) {
        return countDichVuKT(LoaiDichVuKT.XET_NGHIEM, patientId, encounterId);
    }
    
    @GetMapping("/get_xet_nghiem_list")
    public ResponseEntity<?> getXetNghiemList(
    							@RequestParam Optional<String> patientId, 
                                @RequestParam Optional<String> encounterId,
                                @RequestParam Optional<Boolean> includePatient,
                                @RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider,
                                @RequestParam Optional<Integer> start,
                                @RequestParam Optional<Integer> end) {
        
        var lst = getDichVuKTList(LoaiDichVuKT.XET_NGHIEM, patientId, encounterId, 
        							includePatient, includeEncounter, includeServiceProvider, 
        							start, end);

        var result = transform(lst, x -> new XetNghiem(x, false));
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_xet_nghiem_by_id/{id}")
    public ResponseEntity<?> getXetNghiemById(@PathVariable String id,
                                @RequestParam Optional<Boolean> includePatient,
					    		@RequestParam Optional<Boolean> includeEncounter,
								@RequestParam Optional<Boolean> includeServiceProvider) {
					    	
        var obj = procedureDao.read(new IdType(id));
        if(isXetNghiem(obj)) {
        	updateReferenceResource(obj, includePatient, includeEncounter, includeServiceProvider);
        	var xetNghiem = new XetNghiem(obj, true);        	        	
            return ResponseEntity.ok(xetNghiem);
        }
        return new ResponseEntity<>("No xetNghiem with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/save_xet_nghiem")
    public ResponseEntity<?> saveXetNghiem(@RequestBody XetNghiem dto) {
        try {
            var serviceRequest = saveDichVuKT(dto);
            var xetNghiem = new XetNghiem(serviceRequest, true);
            var result = mapOf("success", true, "xetNghiem", xetNghiem);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save xetNghiem: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
