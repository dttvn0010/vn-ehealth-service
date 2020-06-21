package vn.ehealth.cdr.controller.helper;

import org.hl7.fhir.r4.model.MedicationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;
import vn.ehealth.utils.MongoUtils;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

import java.util.List;


@Service
public class MedicationRequestHelper {

    @Autowired private MedicationRequestDao medicationRequestDao;
    @Autowired private EncounterHelper encounterHelper;
    
    public List<MedicationRequest> getMedicationRequestByDonThuoc(String soDon) {
        var params = mapOf(
                        "groupIdentifier.system", (Object) IdentifierSystem.MEDICATION_REQUEST,
                        "groupIdentifier.value", soDon
                    );
        
        return medicationRequestDao.searchResource(MongoUtils.createQuery(params));
    }
    
    
    public void saveToFhirDb(HoSoBenhAn hsba, List<DonThuocChiTiet> dsDonThuocChiTiet) throws Exception {
        if(hsba == null || dsDonThuocChiTiet == null || dsDonThuocChiTiet.size() == 0) {
            return;
        }
        
        var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
        if(enc == null) return;
        
        // remove old data
        var oldMedicationRequests = getMedicationRequestByDonThuoc(dsDonThuocChiTiet.get(0).soDon);
        oldMedicationRequests.forEach(x -> medicationRequestDao.remove(x.getIdElement()));
                
        // create new data
        for(var dtct : dsDonThuocChiTiet) {
            var medReq = dtct.toFHir(enc);
            medicationRequestDao.create(medReq);
        }       
    }
}
