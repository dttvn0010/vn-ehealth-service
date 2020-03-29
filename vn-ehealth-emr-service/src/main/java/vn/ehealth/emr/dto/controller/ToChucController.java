package vn.ehealth.emr.dto.controller;

import java.util.List;

import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.rest.param.TokenParam;
import vn.ehealth.emr.model.dto.CoSoKhamBenh;
import vn.ehealth.emr.model.dto.KhoaDieuTri;
import vn.ehealth.emr.model.dto.ToChuc;
import vn.ehealth.hl7.fhir.core.util.Constants.CodeSystemValue;
import vn.ehealth.hl7.fhir.core.util.Constants.LoaiToChuc;
import vn.ehealth.hl7.fhir.core.util.FPUtil;
import vn.ehealth.hl7.fhir.provider.dao.impl.OrganizationDao;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;
import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/to_chuc")
public class ToChucController {

    private static Logger logger = LoggerFactory.getLogger(ToChucController.class);
    @Autowired private OrganizationDao organizationDao;
    
    private List<Organization> getOrganizations(String maLoaiToChuc, String parentId) {
        var params = mapOf("type", new TokenParam(CodeSystemValue.LOAI_TO_CHUC, maLoaiToChuc));
        
        if(parentId != null) {
            params.put("partOf", ResourceType.Organization + "/" + parentId);
        }
        
        var lst = organizationDao.search(params);
        lst = FPUtil.filter(lst, x -> x instanceof Organization);
        return transform(lst, x -> (Organization)x);        
    }
    
    private Organization saveToChuc(ToChuc dto) {
        if(dto != null) {
            var obj = dto.toFhir();
            if(obj.hasId()) {
                return organizationDao.update(obj, obj.getIdElement());
            }else {
                return organizationDao.create(obj);
            }
        }
        return null;
    }
    
    // ========================================= Co so kham benh ======================================
    
    private boolean isCoSoKhamBenh(Organization obj) {
        if(obj != null && obj.hasType()) {
            for(var concept : obj.getType()) {
                boolean isCskb = conceptHasCode(concept, LoaiToChuc.CO_SO_KHAM_BENH, 
                                                CodeSystemValue.LOAI_TO_CHUC);                
                if(isCskb) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/get_cskb/{id}")
    public ResponseEntity<?> getCoSoKhamBenh(@PathVariable String id) {
        var obj = organizationDao.read(createIdType(id));
        if(isCoSoKhamBenh(obj)) {
            return ResponseEntity.ok(new CoSoKhamBenh(obj));
        }
        return new ResponseEntity<>("No coSoKhamBenh with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/get_cskb_list")
    public ResponseEntity<?> getCoSoKhamBenhList() {
        var lst = getOrganizations(LoaiToChuc.CO_SO_KHAM_BENH, null);
        var result = transform(lst, x -> new CoSoKhamBenh(x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save_cskb")
    public ResponseEntity<?> saveCoSoKhamBenh(@RequestBody CoSoKhamBenh dto) {
        try {
            var obj = saveToChuc(dto);
            var cskb = new CoSoKhamBenh(obj);
            var result = mapOf("success", true, "coSoKhamBenh", cskb);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save coSoKhamBenh: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    // ========================================= Khoa dieu tri ======================================
    
    private boolean isKhoaDieuTri(Organization obj) {
        if(obj != null && obj.hasType()) {
            for(var concept : obj.getType()) {
                boolean isKhoadt = conceptHasCode(concept, LoaiToChuc.KHOA_DIEU_TRI, 
                                                CodeSystemValue.LOAI_TO_CHUC);                
                if(isKhoadt) return true;
            }
        }
        return false;
    }
    
    @GetMapping("/get_khoadt/{id}")
    public ResponseEntity<?> getKhoaDieuTri(@PathVariable String id) {
        var obj = organizationDao.read(createIdType(id));
        if(isKhoaDieuTri(obj)) {
            return ResponseEntity.ok(new KhoaDieuTri(obj));
        }
        return new ResponseEntity<>("No khoaDieuTri with id:" + id, HttpStatus.BAD_REQUEST);
    }
    
    @GetMapping("/get_khoadt_list_by_cskb/{id}")
    public ResponseEntity<?> getKhoaDieuTriByCskb(@PathVariable String id) {
        var lst = getOrganizations(LoaiToChuc.KHOA_DIEU_TRI, id);
        var result = transform(lst, x -> new KhoaDieuTri(x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save_khoadt")
    public ResponseEntity<?> saveKhoaDieuTri(@RequestBody KhoaDieuTri dto) {
        try {
            var obj = saveToChuc(dto);
            var khoa = new KhoaDieuTri(obj);
            var result = mapOf("success", true, "khoaDieuTri", khoa);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save khoaDieuTri: ", e);
            var result = mapOf("success", false, "error", e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
