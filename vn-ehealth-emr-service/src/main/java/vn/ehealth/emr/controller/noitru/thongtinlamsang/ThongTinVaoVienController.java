package vn.ehealth.emr.controller.noitru.thongtinlamsang;

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
import vn.ehealth.emr.constants.EmrConstants.ObservationCodes.ThongTinVaoVien;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.emr.helper.diagnostic.ObservationHelper;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/thongtin_vaovien")
public class ThongTinVaoVienController {
	
	@Autowired private EncounterDao encounterDao;
	@Autowired private PractitionerDao practitionerDao;
	@Autowired private ObservationHelper observationHelper;
	
	@GetMapping("/get")
	public ResponseEntity<?> getThongTinVaoVien(@RequestParam String encounterId) {
		
		var lyDoVaoVienObs = observationHelper.getLastObservation(encounterId, ThongTinVaoVien.LY_DO_VAO_VIEN);
		var vaoNgayThuObs = observationHelper.getLastObservation(encounterId, ThongTinVaoVien.VAO_NGAY_THU);
		var vaoLanThuObs = observationHelper.getLastObservation(encounterId, ThongTinVaoVien.VAO_LAN_THU);
		
		var result = mapOf(
					"lyDoVaoVienObs", ObservationDTO.fromFhir(lyDoVaoVienObs),
					"vaoNgayThuObs", ObservationDTO.fromFhir(vaoNgayThuObs),
					"vaoLanThuObs",  ObservationDTO.fromFhir(vaoLanThuObs)
				);
		
		return ResponseEntity.ok(result);
		
	}
	
	static class ThongTinVaoVienBody {
		public String lyDoVaoVien;
		public String vaoNgayThu;
		public String vaoLanThu;
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> saveThongTinVaoVien(@RequestParam String encounterId, 
								@RequestBody ThongTinVaoVienBody body) {
		try {
			var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
			var user = UserUtil.getCurrentUser();
			var practitionerId = user.fhirPractitionerId;
			var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
			
			observationHelper.saveObservation(encounter, practitioner, ThongTinVaoVien.LY_DO_VAO_VIEN, new StringType(body.lyDoVaoVien));
			
			if(!StringUtils.isBlank(body.vaoNgayThu)) {
				int vaoNgayThu = Integer.valueOf(body.vaoNgayThu);
				observationHelper.saveObservation(encounter, practitioner, ThongTinVaoVien.VAO_NGAY_THU, new IntegerType(vaoNgayThu));
			}
			
			if(!StringUtils.isBlank(body.vaoLanThu)) {
				int vaoLanThu = Integer.valueOf(body.vaoLanThu);
				observationHelper.saveObservation(encounter, practitioner, ThongTinVaoVien.VAO_LAN_THU, new IntegerType(vaoLanThu));
			}			
			
			return ResponseEntity.ok(mapOf("success", true));
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}
		
	}
	
}
