package vn.ehealth.cdr.controller.helper;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.patient.dao.impl.PatientDao;
import vn.ehealth.utils.MongoUtils;

@Service
public class PatientHelper {

    @Autowired private PatientDao patientDao;
    
    public Patient getPatientBySobhyt(String sobhyt) {
        
        var params = mapOf( 
                "identifier.value", (Object) sobhyt,
                "identifier.system",  IdentifierSystem.INSURANCE_NUMBER                            
            );

        var query = MongoUtils.createQuery(params);
        return patientDao.getResource(query);
    }
}
