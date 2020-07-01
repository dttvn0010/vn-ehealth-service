package vn.ehealth.cdr.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.auth.utils.UserUtil;
import vn.ehealth.cdr.controller.helper.MedicationRequestHelper;
import vn.ehealth.cdr.controller.helper.ProcedureHelper;
import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.model.HoSoBenhAn;
import vn.ehealth.cdr.model.component.CanboYteDTO;
import vn.ehealth.cdr.model.dto.YlenhDTO;
import vn.ehealth.cdr.service.CanboYteService;
import vn.ehealth.cdr.service.DichVuKyThuatService;
import vn.ehealth.cdr.service.DonThuocService;
import vn.ehealth.cdr.service.HoSoBenhAnService;
import vn.ehealth.cdr.service.YlenhService;
import vn.ehealth.hl7.fhir.core.util.FhirUtil;
import vn.ehealth.hl7.fhir.core.util.ResponseUtil;
import vn.ehealth.hl7.fhir.core.util.StringUtil;
import vn.ehealth.hl7.fhir.core.util.Constants.IdentifierSystem;
import vn.ehealth.hl7.fhir.core.util.DateUtil;
import vn.ehealth.hl7.fhir.ehr.dao.impl.EncounterDao;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;


@RestController
@RequestMapping("/api/ylenh")
public class YlenhController {
    
    @Autowired private EncounterDao encounterDao;
    @Autowired private ProcedureHelper procedureHelper;
    @Autowired private MedicationRequestHelper medicationRequestHelper;
    
    @Autowired private HoSoBenhAnService hoSoBenhAnService;
    @Autowired private YlenhService ylenhService;
    @Autowired private DonThuocService donThuocService;
    @Autowired private DichVuKyThuatService dichVuKyThuatService;
    @Autowired private CanboYteService canboYteService;
    
    @PostMapping("/create_ylenh/{encounterId}")
    public ResponseEntity<?> createYlenh(@PathVariable String encounterId, @RequestBody YlenhDTO body) {
        try {
            var encounter = encounterDao.read(FhirUtil.createIdType(encounterId));
            var medicalRecord = FhirUtil.findIdentifierBySystem(encounter.getIdentifier(), IdentifierSystem.MEDICAL_RECORD);
            var user = UserUtil.getCurrentUser().orElse(null);
            var canboYteId = user != null? user.canBoYteId : null;
            CanboYte canboYte = null;
            
            if(canboYteId != null) {
                canboYte = canboYteService.getById(new ObjectId(canboYteId)).orElse(null);
            }
            
            HoSoBenhAn hsba = null;
            
            if(medicalRecord != null) {
                String maYte = medicalRecord.getValue();
                hsba = hoSoBenhAnService.getByMaYte(maYte).orElse(null);
            }
            
            if(hsba == null) {
                throw new Exception("No hsba with encounterId=" + encounterId);
            }
            
            if(hsba.chanDoan == null) {
                hsba.chanDoan = new HoSoBenhAn.ChanDoan();
            }
            
            var ylenh = body.generateYlenh();
            ylenh.idhis = StringUtil.generateUUID();
            ylenh.hoSoBenhAnRef = HoSoBenhAn.toEmrRef(hsba);
            ylenh.coSoKhamBenhRef = hsba.coSoKhamBenhRef;
            ylenh.benhNhanRef = hsba.benhNhanRef;
            ylenh.bacSiRaYlenh = CanboYteDTO.fromCanboYte(canboYte);
            ylenh.ngayRaYlenh = new Date();
            ylenh = ylenhService.save(ylenh);
            
            if(ylenh.dmMaBenhChanDoan != null) {                
                hsba.chanDoan.dmMaBenhChanDoanDieuTriChinh = ylenh.dmMaBenhChanDoan;
                hsba = hoSoBenhAnService.save(hsba);
            }            

            var donThuoc = body.generateDonThuoc();
            donThuoc.soDon = StringUtil.generateUUID();
            donThuoc = donThuocService.createOrUpdate(ylenh, donThuoc);
            
            if(donThuoc.dsDonThuocChiTiet.size() > 0) {                
                medicationRequestHelper.saveToFhirDb(hsba, donThuoc.getDsDonThuocChiTiet());
            }
            
            var dsDvkt = body.generateDsDichVuKyThuat();
            for(var dvkt : dsDvkt) {
                dvkt = dichVuKyThuatService.createOrUpdate(ylenh, dvkt);
                procedureHelper.saveToFhirDb(ylenh, dvkt);
            }
           
            return ResponseEntity.ok(mapOf("success", true));
            
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/count/{encounterId}")
    public ResponseEntity<?> count(@PathVariable String encounterId,
            @RequestParam Optional<String> ngayRaYlenh, 
            @RequestParam Optional<String> maLoaiYlenh){
        
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
            
            Date ngayBatDau = null;
            Date ngayKetThuc = null;
            
            if(ngayRaYlenh.isPresent()) {
                ngayBatDau = DateUtil.parseStringToDate(ngayRaYlenh.get(), "dd/MM/yyyy");
                if(ngayBatDau != null) {
                    var cal = Calendar.getInstance();
                    cal.setTime(ngayBatDau);
                    cal.add(Calendar.DATE, 1);
                    ngayKetThuc = cal.getTime();
                }
            }
                        
            var count = ylenhService.countByLoaiAndNgayRaYlenh(hsba.id, maLoaiYlenh.orElse(""), ngayBatDau, ngayKetThuc);
            return ResponseEntity.ok(mapOf("success", true, "count", count));
        }catch(Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    @GetMapping("/get_list/{encounterId}")
    public ResponseEntity<?> getList(@PathVariable String encounterId,
    		@RequestParam Optional<String> ngayRaYlenh, 
    		@RequestParam Optional<String> maLoaiYlenh,
    		@RequestParam Optional<Integer> start,
            @RequestParam Optional<Integer> count){
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
            
            Date ngayBatDau = null;
            Date ngayKetThuc = null;
            
            if(ngayRaYlenh.isPresent()) {
            	ngayBatDau = DateUtil.parseStringToDate(ngayRaYlenh.get(), "dd/MM/yyyy");
            	if(ngayBatDau != null) {
                	var cal = Calendar.getInstance();
                    cal.setTime(ngayBatDau);
                    cal.add(Calendar.DATE, 1);
                    ngayKetThuc = cal.getTime();
            	}
            }
                        
    		var lst = ylenhService.getByLoaiAndNgayRaYlenh(hsba.id, maLoaiYlenh.orElse(""), ngayBatDau, ngayKetThuc, 
    		                            start.orElse(-1), count.orElse(-1));
            return ResponseEntity.ok(mapOf("success", true, "dsYlenh", lst));
        }catch(Exception e) {
            return ResponseUtil.errorResponse(e);
        }
    }
    
    @GetMapping("/get_detail/{ylenhId}")
    public ResponseEntity<?> getDetail(@PathVariable String ylenhId) {
        try {
            var ylenh = ylenhService.getById(new ObjectId(ylenhId)).get();
            var dsDVKT = dichVuKyThuatService.getByYlenhId(ylenh.id);
            
            var dsDonThuoc = donThuocService.getByYlenhId(ylenh.id);                      
            var donThuoc = dsDonThuoc.size() > 0? dsDonThuoc.get(0) : null;
        
            var result = mapOf("success", true, "ylenh", ylenh, "dsDVKT", dsDVKT, "donThuoc", donThuoc);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
