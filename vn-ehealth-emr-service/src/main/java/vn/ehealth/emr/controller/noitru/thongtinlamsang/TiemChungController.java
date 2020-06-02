package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.Date;
import java.util.HashMap;

import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.medication.ImmunizationDTO;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.medication.dao.impl.ImmunizationDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;
import vn.ehealth.utils.MongoUtils;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/tiemchung")
public class TiemChungController {
	@Autowired private EncounterDao encounterDao;
	@Autowired private PractitionerDao practitionerDao;
	@Autowired private ImmunizationDao immunizationDao;
	
	static class TiemChungBody {
		public CodingDTO vaccine;
		public String noiTiem;
		public String ngayTiem;
		public String moTaPhanUngSauTiem;
		public String ghiChu;
		
	}
	@GetMapping("/get_list")
	public ResponseEntity<?> getDsTiemChung(@RequestParam String encounterId) {
		var params = new HashMap<String,Object>();
		params.put("encounter.reference", ResourceType.Encounter + "/" + encounterId);
		var lst = immunizationDao.searchResource(MongoUtils.createQuery(params));
		var result = DataConvertUtil.transform(lst, ImmunizationDTO::fromFhir);
		return ResponseEntity.ok(result);
	}

	
	@GetMapping("/delete")
	public ResponseEntity<?> deleteTiemChung(@RequestParam String id,
			@RequestParam String immunizationId) {
		try {
//			var encounter = encounterDao.read(FhirUtil.createIdType(id));
			immunizationDao.remove(FhirUtil.createIdType(immunizationId));
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
		
	@PostMapping("/add")
	public ResponseEntity<?> addTiemChung(@RequestParam String encounterId,  @RequestBody TiemChungBody body) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var user = UserUtil.getCurrentUser().orElse(null);
			
			var imm = new Immunization();
			
			var practitionerId = user != null? user.fhirPractitionerId : null;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			var practitionerRef = FhirUtil.createReference(practitioner);
			practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
			imm.setRecorded(new Date());
					
			imm.setEncounter(FhirUtil.createReference(encounter));
			imm.setPatient(encounter.getSubject());
			imm.setVaccineCode(CodingDTO.toCodeableConcept(body.vaccine, CodeSystemValue.VACCINE_CODE));
			if(body.ngayTiem != null) {
				imm.setOccurrence(new DateTimeType(DateUtil.parseStringToDate(body.ngayTiem.substring(0, 10), "yyyy-MM-dd")));
			}
			
			var ref = new Reference();
			ref.setDisplay(body.noiTiem);
			imm.setLocation(ref);
			
			var reaction = imm.addReaction();
			var ext = reaction.addExtension();
			ext.setValue(new StringType(body.moTaPhanUngSauTiem));
			reaction.setDate(new Date());
			reaction.setReported(true);
			
			
			var note = new Annotation();
			note.setText(body.ghiChu);
			imm.addNote(note);
			
			immunizationDao.create(imm);
			
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
	@PutMapping("/update")
	public ResponseEntity<?> updateTiemChung(@RequestParam String encounterId, 
										@RequestParam String immunizationId,
										@RequestBody TiemChungBody body) {
		try {
			
			var user = UserUtil.getCurrentUser().orElse(null);
			
			var imm = immunizationDao.read(FhirUtil.createIdType(immunizationId));
			
			var practitionerId = user != null? user.fhirPractitionerId : null;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			var practitionerRef = FhirUtil.createReference(practitioner);
			practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
			imm.setRecorded(new Date());
			
			
			imm.setVaccineCode(CodingDTO.toCodeableConcept(body.vaccine, CodeSystemValue.VACCINE_CODE));
			if(body.ngayTiem != null) {
				imm.setOccurrence(new DateTimeType(DateUtil.parseStringToDate(body.ngayTiem.substring(0, 10), "yyyy-MM-dd")));
			}
			
			var ref = new Reference();
			ref.setDisplay(body.noiTiem);
			imm.setLocation(ref);
			
			var reaction = imm.getReactionFirstRep();
			var ext = reaction.getExtensionFirstRep();
			ext.setValue(new StringType(body.moTaPhanUngSauTiem));
			reaction.setDate(new Date());
			reaction.setReported(true);
			
			
			var note = imm.getNoteFirstRep();
			note.setText(body.ghiChu);
			
			imm = immunizationDao.update(imm, imm.getIdElement()); 
			return ResponseEntity.ok(mapOf("success", true));
			
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
}
