package vn.ehealth.emr.controller.diagnostic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/observation")
public class ObservationController {

	private IParser parser = FhirContext.forR4().newJsonParser();
	private Logger log = LoggerFactory.getLogger(ObservationController.class);
	
	@Autowired private PractitionerDao practitionerDao;
	@Autowired private EncounterDao encounterDao;
	@Autowired private ObservationDao observationDao;
	
	private void saveObservation(Practitioner practitioner, Observation obs) {
		try {
			var enc = encounterDao.read(FhirUtil.createIdType(obs.getEncounter()));
			obs.getEncounter().setIdentifier(enc.getIdentifierFirstRep());
			obs.setSubject(enc.getSubject());
			obs.setIssued(new Date());
			if(practitioner != null) {
				obs.setPerformer(List.of(FhirUtil.createReference(practitioner)));
			}
			observationDao.create(obs);
		}catch(Exception e) {
			log.error("Cannot save observation:", obs);
		}
	}
	
	@GetMapping("/get_last_multi")
	public ResponseEntity<?> getLastObservations(@RequestParam String encounterId, @RequestParam String observationCodes) {
		var codes = observationCodes.split(",");
		var result = new HashMap<String, Object>();
		
		for(var code : codes) {
			String[] arr = code.split("\\|");
			if(arr.length == 2) {
				String codeSystem = arr[0];
				String codeValue = arr[1];
				var obs = getLastObservation(encounterId, codeValue, codeSystem);
				result.put(code, ObservationDTO.fromFhir(obs));
			}
		}
		
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/save_multi")
	public ResponseEntity<?> saveObservations(@RequestBody String json) {
				
		try {
			Bundle bundle = (Bundle) parser.parseResource(json);
			var observations = transform(bundle.getEntry(), x -> (Observation) x.getResource());
			var user = UserUtil.getCurrentUser().orElse(null);
			var practitionerId = user != null? user.fhirPractitionerId : null;
            
            var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
            
			observations.forEach(x -> saveObservation(practitioner, x));
			return ResponseEntity.ok(mapOf("success", true));
			
		}catch(Exception e) {
			return ResponseUtil.errorResponse(e);
		}		
	}
	
	private Observation getLastObservation(String encounterId, String codeValue, String codeSystem) {
		var params = new HashMap<String, Object>();
		params.put("encounter.reference", ResourceType.Encounter + "/" + encounterId);
		params.put("code.coding.code", codeValue);
		params.put("code.coding.system", codeSystem);
		var sort = new Sort(Sort.Direction.DESC, "issued");
		var obs = observationDao.searchResource(MongoUtils.createQuery(params), 0, 1, sort);
		return obs.size() > 0? obs.get(0) : null;
	}
}
