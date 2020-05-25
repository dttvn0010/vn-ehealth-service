package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.constants.EmrConstants.ObservationCodes.QuaTrinhBenhLy;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.emr.helper.diagnostic.ObservationHelper;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/quatrinh_benhly")
public class QuatrinhBenhlyController {
	@Autowired private EncounterDao encounterDao;
	@Autowired private PractitionerDao practitionerDao;
	@Autowired private ObservationHelper observationHelper;
	
	@GetMapping("/get")
	public ResponseEntity<?> getQuaTrinhBenhLy(@RequestParam String encounterId) {
		
		var quaTrinhKhoiPhatObs = observationHelper.getLastObservation(encounterId, QuaTrinhBenhLy.QUA_TRINH_KHOI_PHAT);
		var mucDoVaDienBienObs = observationHelper.getLastObservation(encounterId, QuaTrinhBenhLy.MUC_DO_VA_DIEN_BIEN);
		var viTriObs = observationHelper.getLastObservation(encounterId, QuaTrinhBenhLy.VI_TRI);
		var tongQuanObs = observationHelper.getLastObservation(encounterId, QuaTrinhBenhLy.TONG_QUAN);
		
		var result = mapOf(
					"quaTrinhKhoiPhatObs", ObservationDTO.fromFhir(quaTrinhKhoiPhatObs),
					"mucDoVaDienBienObs", ObservationDTO.fromFhir(mucDoVaDienBienObs),
					"viTriObs",  ObservationDTO.fromFhir(viTriObs),
					"tongQuanObs", ObservationDTO.fromFhir(tongQuanObs)
				);
		
		return ResponseEntity.ok(result);
		
	}
	static class QuaTrinhBenhLyBody {
		public String quaTrinhKhoiPhat;
		public String mucDoVaDienBien;
		public String viTri;
		public String tongQuan;
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveQuaTrinhBenhLy(@RequestParam String encounterId, 
								@RequestBody QuaTrinhBenhLyBody body) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var user = UserUtil.getCurrentUser().orElse(null);
			var practitionerId = user != null? user.fhirPractitionerId : null;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			observationHelper.saveObservation(encounter, practitioner, QuaTrinhBenhLy.QUA_TRINH_KHOI_PHAT, new StringType(body.quaTrinhKhoiPhat));
			observationHelper.saveObservation(encounter, practitioner, QuaTrinhBenhLy.MUC_DO_VA_DIEN_BIEN, new StringType(body.mucDoVaDienBien));
			observationHelper.saveObservation(encounter, practitioner, QuaTrinhBenhLy.VI_TRI, new StringType(body.viTri));
			observationHelper.saveObservation(encounter, practitioner, QuaTrinhBenhLy.TONG_QUAN, new StringType(body.tongQuan));
			
			
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
		
	}
}
