package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.mapOf;

import org.hl7.fhir.r4.model.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.emr.constants.EmrConstants.ObservationCodes.ChucNangSong;
import vn.ehealth.emr.dto.diagnostic.ObservationDTO;
import vn.ehealth.emr.helper.diagnostic.ObservationHelper;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.PractitionerDao;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/chucnangsong")
public class ThongTinChucNangSongController {

    @Autowired private EncounterDao encounterDao;
    @Autowired private PractitionerDao practitionerDao;
    @Autowired private ObservationHelper observationHelper;
    
    @GetMapping("/get")
    public ResponseEntity<?> getThongTinChucNangSong(@RequestParam String encounterId) {
        
        var heartRateObs = observationHelper.getLastObservation(encounterId, ChucNangSong.HEART_RATE);
        var bpSystolicObs = observationHelper.getLastObservation(encounterId, ChucNangSong.BP_SYSTOLIC);
        var bpDiastolicObs = observationHelper.getLastObservation(encounterId, ChucNangSong.BP_DIASTOLIC);
        var respirationRateObs = observationHelper.getLastObservation(encounterId, ChucNangSong.RESPIRATION_RATE);
        var bodyTemperatureObs = observationHelper.getLastObservation(encounterId, ChucNangSong.BODY_TEMPERATURE);
        
        var result = mapOf(
                    "heartRateObs", ObservationDTO.fromFhir(heartRateObs),
                    "bpSystolicObs", ObservationDTO.fromFhir(bpSystolicObs),
                    "bpDiastolicObs", ObservationDTO.fromFhir(bpDiastolicObs),
                    "respirationRateObs",  ObservationDTO.fromFhir(respirationRateObs),
                    "bodyTemperatureObs", ObservationDTO.fromFhir(bodyTemperatureObs)
                );
        
        return ResponseEntity.ok(result);
        
    }
    
    static class ThongTinChucNangSongBody {
        public Double mach;
        public Double huyetApCao;
        public Double huyetApThap;
        public Double nhipTho;
        public Double nhietDo;        
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> saveThongTinChucNangSong(@RequestParam String encounterId, 
                                @RequestBody ThongTinChucNangSongBody body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var user = UserUtil.getCurrentUser();
            var practitionerId = user.fhirPractitionerId;
            var practitioner = practitionerDao.read(FhirUtil.createIdType(practitionerId));
            
            if(body.mach != null) {
                var quant = new Quantity();
                quant.setValue(body.mach);
                observationHelper.saveObservation(encounter, practitioner, ChucNangSong.HEART_RATE, quant);
            }
            
            if(body.huyetApCao != null) {
                var quant = new Quantity();
                quant.setValue(body.huyetApCao);
                observationHelper.saveObservation(encounter, practitioner, ChucNangSong.BP_SYSTOLIC, quant);
            }
            
            if(body.huyetApThap != null) {
                var quant = new Quantity();
                quant.setValue(body.huyetApThap);
                observationHelper.saveObservation(encounter, practitioner, ChucNangSong.BP_DIASTOLIC, quant);
            }
            
            if(body.nhipTho != null) {
                var quant = new Quantity();
                quant.setValue(body.nhipTho);
                observationHelper.saveObservation(encounter, practitioner, ChucNangSong.RESPIRATION_RATE, quant);
            }
            
            if(body.nhietDo != null) {
                var quant = new Quantity();
                quant.setValue(body.nhietDo);
                observationHelper.saveObservation(encounter, practitioner, ChucNangSong.BODY_TEMPERATURE, quant);
            }
            
            return ResponseEntity.ok(mapOf("success", true));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
        
    }
}
