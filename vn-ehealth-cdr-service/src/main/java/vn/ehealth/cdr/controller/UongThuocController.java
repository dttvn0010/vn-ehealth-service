package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.cdr.model.DonThuocChiTiet;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.UongThuoc;
import vn.ehealth.cdr.service.DonThuocChiTietService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;

@RestController
@RequestMapping("/api/uongthuoc")
public class UongThuocController {

    @Autowired private EncounterDao encounterDao;
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private DonThuocChiTietService donThuocChiTietService;
    
    
    private UongThuoc newUongThuoc(DonThuocChiTiet dtct, String maThoiDiemUongThuoc) {
        if(dtct == null) return null;
        
        var uongThuoc = new UongThuoc();
        uongThuoc.dmThuoc = dtct.dmThuoc;
        uongThuoc.dmDuongDungThuoc = dtct.dmDuongDungThuoc;
        uongThuoc.bacSiChiDinh = dtct.bacSiKeDon;
        
        if(dtct.dsTanSuatDungThuoc != null) {
            for(var tsdt : dtct.dsTanSuatDungThuoc) {
                if(tsdt.dmThoiDiemDungThuoc == null) continue;
                
                if(maThoiDiemUongThuoc.equals(tsdt.dmThoiDiemDungThuoc.ma)) {
                    uongThuoc.soLuong = tsdt.soLuong;
                    uongThuoc.dmThoiDiemDungThuoc = tsdt.dmThoiDiemDungThuoc;
                    break;
                }
            }
        }
        
        return uongThuoc;
    }
    
    
    @GetMapping("/get_ds_uongthuoc_ngay/{encounterId}")
    public ResponseEntity<?> getDsUongThuoc(@PathVariable String encounterId,
                            @RequestParam String maThoiDiemUongThuoc,
                            @RequestParam Optional<Integer> start,
                            @RequestParam Optional<Integer> count) {
        
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            var date = new Date();
            var dsDonThuocChiTiet = donThuocChiTietService.getDsUongThuoc(hsba.id, date, maThoiDiemUongThuoc,
                                        start.orElse(-1), count.orElse(-1));
            
            var lst = FPUtil.transform(dsDonThuocChiTiet, x -> newUongThuoc(x, maThoiDiemUongThuoc));
            return ResponseEntity.ok(lst);
        }catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
