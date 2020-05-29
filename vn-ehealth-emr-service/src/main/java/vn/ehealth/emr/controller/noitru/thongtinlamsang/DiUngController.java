package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hl7.fhir.r4.model.Age;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory;
import org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.dto.base.CodingDTO;
import vn.ehealth.emr.dto.clinical.AllergyIntoleranceDTO;
import vn.ehealth.hl7.fhir.clinical.dao.impl.AllergyIntoleranceDao;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;
import vn.ehealth.utils.MongoUtils;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/di_ung")
public class DiUngController {

	@Autowired private AllergyIntoleranceDao allergyIntoleranceDao;
	@Autowired private EncounterDao encounterDao;
	@Autowired private PractitionerDao practitionerDao;
	
	static class DiUngBody { 
		static class DiUngItem {
			public CodingDTO sanPham; // ~ substance
			public CodingDTO phanUng; // ~ manifestation
			public CodingDTO viTri; // ~ exposureRoute
			public String moTaPhanUng;	// ~ note
		}
		public CodingDTO chatDiUng;  //~ code
		public CodingDTO mucDoNghiemTrong; // --> criticality
		public CodingDTO loaiDiUng;	// --> category
		public CodingDTO trangThai; // --> clinicalStatus
		public Integer tuoiDiUng; // --> onSet
		//public CodingDTO bacSiDieuTri; // Dang thieu truong participant
		public List<DiUngItem> items;
	}
	
	@PostMapping("/add")
	public ResponseEntity<?> addDiUng(@RequestParam String encounterId, @RequestBody DiUngBody body) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var user = UserUtil.getCurrentUser().orElse(null);
			var practitionerId = user != null? user.fhirPractitionerId : null;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			var allergy = new AllergyIntolerance();
			//common
			allergy.setEncounter(FhirUtil.createReference(encounter));			// Dot kham benh
			allergy.setPatient(encounter.getSubject());							// Benh nhan (subject/patient)
			
			if(practitioner != null) {
				var practitionerRef = FhirUtil.createReference(practitioner);		
				practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
				allergy.setRecorder(practitionerRef);								// Nguoi ghi/thuc hien
			}
			allergy.setRecordedDate(new Date());								// Ngay ghi/thuc hien
						
			// allergy info
			allergy.setCriticality(AllergyIntoleranceCriticality.fromCode(body.mucDoNghiemTrong.code));
			allergy.addCategory(AllergyIntoleranceCategory.fromCode(body.loaiDiUng.code));
			allergy.setClinicalStatus(CodingDTO.toCodeableConcept(body.trangThai, CodeSystemValue.ALLERGY_INTOLERANCE_CLINICAL));
			allergy.setCode(CodingDTO.toCodeableConcept(body.chatDiUng, CodeSystemValue.ALLERGY_INTOLERANCE_CODE));
			if(body.tuoiDiUng != null) {
				var age = new Age();
				age.setValue(body.tuoiDiUng);
				allergy.setOnset(age);
			}
			if(body.items != null) {
				for(var item : body.items) {
					var reaction = new AllergyIntolerance.AllergyIntoleranceReactionComponent();
					reaction.setSubstance(CodingDTO.toCodeableConcept(item.sanPham, CodeSystemValue.SUBSTANCE_CODE));
					reaction.addManifestation(CodingDTO.toCodeableConcept(item.phanUng, CodeSystemValue.CLINICAL_FINDING));
					reaction.setExposureRoute(CodingDTO.toCodeableConcept(item.viTri, CodeSystemValue.ROUTE_CODE));
					var ann = reaction.addNote();
					ann.setText(item.moTaPhanUng);
				}
			}
			allergyIntoleranceDao.create(allergy);
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
	}
	
	@GetMapping("/get_list")
	public ResponseEntity<?> getDsDiUng(@RequestParam String encounterId) {
		var params = new HashMap<String,Object>();
		params.put("encounter.reference", ResourceType.Encounter + "/" + encounterId);
		var lst = allergyIntoleranceDao.searchResource(MongoUtils.createQuery(params));
		var result = DataConvertUtil.transform(lst, AllergyIntoleranceDTO::fromFhir);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/delete")
	public ResponseEntity<?> deleteDiUng(@RequestParam String id){
		allergyIntoleranceDao.remove(FhirUtil.createIdType(id));
		return ResponseEntity.ok(mapOf("success", true));
	}
}