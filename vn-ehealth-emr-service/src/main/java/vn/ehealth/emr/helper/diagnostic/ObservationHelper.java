package vn.ehealth.emr.helper.diagnostic;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.diagnostic.dao.impl.ObservationDao;
import vn.ehealth.utils.MongoUtils;

@Service
public class ObservationHelper {

	@Autowired private ObservationDao observationDao;

	
	public Observation getLastObservation(String encounterId, String observationCode) {
		String[] arr = observationCode.split("\\|");
		String codeSystem = arr[0], code = arr[1];
		
		var params = new HashMap<String, Object>();
		params.put("encounter.reference", ResourceType.Encounter + "/" + encounterId);
		params.put("code.coding.code", code);
		params.put("code.coding.system", codeSystem);
		var sort = new Sort(Sort.Direction.DESC, "issued");
		var obs = observationDao.searchResource(MongoUtils.createQuery(params), 0, 1, sort);
		return obs.size() > 0? obs.get(0) : null;
	}
	
	public Observation saveObservation(@Nonnull Encounter encounter, Practitioner practitioner, 
			String observationCode, Type value) throws Exception {
		
		var obs = new Observation();
		
		// Code
		String[] arr = observationCode.split("\\|");
		String codeSystem = arr[0], code = arr[1], name = arr[2];
		obs.setCode(FhirUtil.createCodeableConcept(code, name, codeSystem));
		
		// Encounter
		var encounterRef = FhirUtil.createReference(encounter);
		encounterRef.setIdentifier(encounter.getIdentifierFirstRep());
		obs.setEncounter(encounterRef);
		
		// Subject
		obs.setSubject(encounter.getSubject());
		
		// Issued
		obs.setIssued(new Date());
		
		// Practitioner
		if(practitioner != null) {
			var practitionerRef = FhirUtil.createReference(practitioner);
			practitionerRef.setDisplay(practitioner.getNameFirstRep().getText());
			obs.setPerformer(List.of(practitionerRef));
		}
		
		// Value
		obs.setValue(value);
		
		return observationDao.create(obs);
	}
}
