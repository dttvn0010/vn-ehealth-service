package vn.ehealth.cdr.controller;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.dto.ChamSocDTO;
import vn.ehealth.cdr.service.CanboYteService;
import vn.ehealth.cdr.service.ChamSocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.UongThuocService;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/chamsoc")
public class ChamSocController {
    
    @Autowired private EncounterDao encounterDao;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private ChamSocService chamSocService;
    @Autowired private CanboYteService canboYteService;
    @Autowired private UongThuocService uongThuocService;
    
    @PostMapping("/create_chamsoc/{encounterId}")
    public ResponseEntity<?> createChamSoc(@PathVariable String encounterId, @RequestBody ChamSocDTO body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var canboYteId = user != null? user.canBoYteId : null;
            CanboYte canboYte = null;
            
            if(canboYteId != null) {
                canboYte = canboYteService.getById(new ObjectId(canboYteId)).orElse(null);
            }
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            var chamSoc = body.generateChamSoc();
            chamSoc.idhis = StringUtil.generateUUID();
            chamSoc.ytaChamSoc = CanboYteDTO.fromCanboYte(canboYte);
            chamSoc.ngayChamSoc = new Date();
            
            chamSoc = chamSocService.createOrUpdate(hsba, chamSoc);
            
            var dsUongThuoc = body.generateDsUongThuoc();
            for(var uongThuoc : dsUongThuoc) {
                uongThuocService.createOrUpdate(chamSoc, uongThuoc);
            }
            
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    
    @GetMapping("/get_ds_chamsoc")
    public ResponseEntity<?> getDsChamSoc(@RequestParam("hsba_id") String id) {
    
        return ResponseEntity.ok(new ArrayList<>());
    }
    
    @GetMapping("/count/{encounterId}")
    public ResponseEntity<?> count(@PathVariable String encounterId,
                @RequestParam Optional<String> ngayChamSoc,
                @RequestParam Optional<String> maLoaiChamSoc) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
    
            Date ngayBatDau = null;
            Date ngayKetThuc = null;

            if (ngayChamSoc.isPresent()) {
                ngayBatDau = DateUtil.parseStringToDate(ngayChamSoc.get(), "dd/MM/yyyy");
                var cal = Calendar.getInstance();
                cal.setTime(ngayBatDau);
                cal.add(Calendar.DATE, 1);
                ngayKetThuc = cal.getTime();
            }
            
            var count = chamSocService.countByLoaiAndNgayChamSoc(hsba.id, maLoaiChamSoc.orElse(""), ngayBatDau, ngayKetThuc);
            return ResponseEntity.ok(mapOf("success", true, "count", count));
        
        }catch (Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/get_list/{encounterId}")
	public ResponseEntity<?> getList(@PathVariable String encounterId, 
	        @RequestParam Optional<String> ngayChamSoc,
			@RequestParam Optional<String> maLoaiChamSoc, 
			@RequestParam Optional<Integer> start,
			@RequestParam Optional<Integer> count) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(),
					IdentifierSystem.MEDICAL_RECORD);

			HoSoBenhAn hsba = null;

			if (medicalRecord != null) {
				String maYte = medicalRecord.getValue();
				hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
			}

			if (hsba == null) {
				throw new Exception("No hsba with encounterId=" + encounterId);
			}

			Date ngayBatDau = null;
			Date ngayKetThuc = null;

			if (ngayChamSoc.isPresent()) {
				String ngayBatDauSt = ngayChamSoc.get() + " 00:00:00";
				String ngayKetThucSt = ngayChamSoc.get() + " 23:59:59";

				ngayBatDau = DateUtil.parseStringToDate(ngayBatDauSt, "dd/MM/yyyy HH:mm:ss");
				ngayKetThuc = DateUtil.parseStringToDate(ngayKetThucSt, "dd/MM/yyyy HH:mm:ss");
			}

			var lst = chamSocService.getByLoaiAndNgayChamSoc(hsba.id, maLoaiChamSoc.orElse(""), ngayBatDau, ngayKetThuc, 
			                            start.orElse(-1), count.orElse(-1));
			
			return ResponseEntity.ok(mapOf("success", true, "dsChamSoc", lst));

		} catch (Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
    
    @GetMapping("/get_detail/{chamsocId}")
    public ResponseEntity<?> getDetail(@PathVariable String chamsocId) {
        try {
            var chamsoc = chamSocService.getById(new ObjectId(chamsocId)).get();
            var dsUongThuoc = uongThuocService.getByChamSocId(new ObjectId(chamsocId));
            return ResponseEntity.ok(mapOf("success", true, "chamSoc", chamsoc, "dsUongThuoc", dsUongThuoc));
        } catch (Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
}
