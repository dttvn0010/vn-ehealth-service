package vn.ehealth.emr.model;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.service.ServiceFactory;
import vn.ehealth.emr.utils.ObjectIdUtil;
import vn.ehealth.emr.utils.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.dao.util.DaoFactory;

public class EmrDichVuKyThuat {

    public ObjectId emrHoSoBenhAnId;
    public ObjectId emrBenhNhanId;
    public ObjectId emrCoSoKhamBenhId;
    
    public String getEmrHoSoBenhAnId() {
        return ObjectIdUtil.idToString(emrHoSoBenhAnId);
    }
    
    public void setEmrHoSoBenhAnId(String emrHoSoBenhAnId) {
        this.emrHoSoBenhAnId = ObjectIdUtil.stringToId(emrHoSoBenhAnId);            
    }

    public String getEmrBenhNhanId() {
        return ObjectIdUtil.idToString(emrBenhNhanId);
    }

    public void setEmrBenhNhanId(String emrBenhNhanId) {
        this.emrBenhNhanId = ObjectIdUtil.stringToId(emrBenhNhanId);
    }
    
    public String getEmrCoSoKhamBenhId() {
        return ObjectIdUtil.idToString(emrCoSoKhamBenhId);
    }
    
    public void setEmrCoSoKhamBenhId(String emrCoSoKhamBenhId) {
        this.emrCoSoKhamBenhId = ObjectIdUtil.stringToId(emrCoSoKhamBenhId);
    }
    
    @JsonIgnore
    public String getPatientId() {
        var emrBenhNhan = ServiceFactory.getEmrBenhNhanService().getById(this.emrBenhNhanId).orElse(null);
        if(emrBenhNhan != null) {
            var params = mapOf("identifier", new TokenParam(IdentifierSystem.THE_BHYT, emrBenhNhan.sobhyt));
            var patient = DaoFactory.getPatientDao().searchOne(params);
            if(patient != null) {
                return patient.getId();
            }
        }
        return null;
    }
    
    @JsonIgnore 
    public String getEncounterId() {
        var emrHoSoBenhAn = ServiceFactory.getEmrHoSoBenhAnService().getById(this.emrHoSoBenhAnId).orElse(null);
        if(emrHoSoBenhAn != null) {
            var params = mapOf("identifier", new TokenParam(IdentifierSystem.MA_HO_SO, emrHoSoBenhAn.mayte));
            var encounter = DaoFactory.getEncounterDao().searchOne(params);
            if(encounter != null) {
                return encounter.getId();
            }
        }
        return null;
    }
}
