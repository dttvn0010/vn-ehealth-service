package vn.ehealth.cdr.controller.helper;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.utils.MongoUtils;

@Service
public class EncounterHelper {

    @Autowired private EncounterDao encounterDao;
    
    public Encounter getEncounterByMaHsba(String maYte) {
        var params = mapOf(
                "identifier.value", (Object) maYte,
                "identifier.system", IdentifierSystem.MA_HO_SO                      
            );

        var query = MongoUtils.createQuery(params);
        return encounterDao.getResource(query);
    }
    
    public List<Encounter> getVkEncounterList(Encounter hsbaEncounter) {
        if(hsbaEncounter != null) {
            var parent = (Object) (ResourceType.Encounter + "/" + hsbaEncounter.getId());
            var params = mapOf("partOf.reference", parent);    
            return encounterDao.searchResource(MongoUtils.createQuery(params));
        }
        
        return new ArrayList<>();
    }
}
