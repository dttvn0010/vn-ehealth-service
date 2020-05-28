package vn.ehealth.cdr.controller.helper;

import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.List;


@Service
public class MedicationRequestHelper {

    @Autowired private MedicationRequestDao medicationRequestDao;
    
    public List<MedicationRequest> getMedicationRequestByDonThuoc(String soDon) {
        var params = mapOf(
                        "groupIdentifier.system", (Object) IdentifierSystem.MEDICATION_REQUEST,
                        "groupIdentifier.value", soDon
                    );
        
        return medicationRequestDao.searchResource(MongoUtils.createQuery(params));
    }
    
}
