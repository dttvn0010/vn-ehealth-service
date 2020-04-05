package vn.ehealth.cdr.controller;

import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.cdr.controller.helper.EncounterHelper;
import vn.ehealth.cdr.model.DonThuoc;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.utils.*;
import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.medication.dao.impl.MedicationRequestDao;


@RestController
@RequestMapping("/api/donthuoc")
public class DonThuocController {
    
    private Logger log = LoggerFactory.getLogger(DonThuocController.class);
    
    @Autowired private DonThuocService donThuocService;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private MedicationRequestDao medicationRequestDao;
    @Autowired private EncounterHelper encounterHelper;
    
    private ObjectMapper objectMapper = CDRUtils.createObjectMapper();
    
    
    @GetMapping("/get_ds_donthuoc")
    public ResponseEntity<?> getDsDonThuoc(@RequestParam("hsba_id") String id) {
        var donthuocList = donThuocService.getByHoSoBenhAnId(new ObjectId(id));
        return ResponseEntity.ok(donthuocList);
    }
    
    private void saveToFhirDb(HoSoBenhAn hsba, List<DonThuoc> donThuocList) {
        if(hsba == null) return;
        
        try {
            var enc = encounterHelper.getEncounterByMaHsba(hsba.maYte);
            if(enc == null) return;
            
            for(var donthuoc : donThuocList) {
                if(donthuoc.dsDonThuocChiTiet == null) continue;                
                
                for(var dtct : donthuoc.dsDonThuocChiTiet) {
                    dtct.bacSiKeDon = donthuoc.bacSiKeDon;
                    dtct.ngayKeDon = donthuoc.ngayKeDon;
                    dtct.soDon = donthuoc.soDon;
                    var medReq = dtct.toFHir(enc);
                    medicationRequestDao.create(medReq);
                }
            }
        }catch(Exception e) {
            log.error("Cannot save donthuoc from hsba id=" + hsba.getId() + " to fhir DB", e);
        }        
    }
    
    @PostMapping("/create_or_update_don_thuoc")
    public ResponseEntity<?> createOrUpdateDonThuocFromHIS(@RequestBody String jsonSt) {
        try {
            jsonSt = JsonUtil.preprocess(jsonSt);
            var map = JsonUtil.parseJson(jsonSt);
            var maTraoDoiHsba = (String) map.get("maTraoDoiHoSo");
            var hsba = hoSoBenhAnService.getByMaTraoDoi(maTraoDoiHsba).orElse(null);
            
            if(hsba == null) {
                throw new Exception(String.format("hoSoBenhAn maTraoDoi=%s not found", maTraoDoiHsba));
            }
            
            var dtObjList = CDRUtils.getFieldAsList(map, "dsDonThuoc");
            var dtList = FPUtil.transform(dtObjList, x -> objectMapper.convertValue(x, DonThuoc.class));
            donThuocService.createOrUpdateFromHIS(hsba, dtList, jsonSt);
            
            saveToFhirDb(hsba, dtList);
            
            var result = DataConvertUtil.mapOf(
                "success" , true,
                "dtList", dtList  
            );
            
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            return CDRUtils.errorResponse(e);
        }
    }    
}
