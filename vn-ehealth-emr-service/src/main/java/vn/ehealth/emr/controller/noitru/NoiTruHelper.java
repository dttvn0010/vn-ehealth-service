package vn.ehealth.emr.controller.noitru;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf3;

import java.util.List;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;

@Service
public class NoiTruHelper {

	@Autowired private EncounterDao encounterDao;
	
	public Encounter getCurrentEncounter(String patientId) {
		
		var status = List.of(
    		EncounterStatus.ARRIVED,
    		EncounterStatus.TRIAGED,
    		EncounterStatus.INPROGRESS
    	);
    	
        var params =  mapOf3("status", "$in", status);
        params.put("subject.reference", ResourceType.Patient + "/" + patientId);
        return encounterDao.getResource(MongoUtils.createCriteria(params));        
	}
	
}
