package vn.ehealth.cdr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.cdr.model.CoSoKhamBenh;
import vn.ehealth.cdr.service.CoSoKhamBenhService;
import vn.ehealth.cdr.utils.EmrUtils;
import vn.ehealth.hl7.fhir.provider.dao.impl.OrganizationDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/coso_khambenh")
public class CoSoKhamBenhController {
    
    @Autowired private CoSoKhamBenhService coSoKhamBenhService;
    @Autowired private OrganizationDao organizationDao;
    
    private void saveToFhirDb(CoSoKhamBenh cskb) {
        if(cskb == null) return;
        
        try {
            var orgInDb = cskb.getOrganizationInDB();
            var org = cskb.toFhir();
            
            if(orgInDb != null) {
                organizationDao.update(org, orgInDb.getIdElement());
            }else {
                organizationDao.create(org);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    @PostMapping("/create_or_update_cskb")
    public ResponseEntity<?> createOrUpdateCskb(@RequestBody CoSoKhamBenh cskb) {
        try {
            cskb.id = coSoKhamBenhService.getByMa(cskb.ma).map(x -> x.id).orElse(null);
            cskb = coSoKhamBenhService.save(cskb);
            
            saveToFhirDb(cskb);
                        
            var result = mapOf("success", true, "cskb", cskb);
            return ResponseEntity.ok(result);            
            
        }catch(Exception e) {
            return EmrUtils.errorResponse(e);
        }
    }
}
