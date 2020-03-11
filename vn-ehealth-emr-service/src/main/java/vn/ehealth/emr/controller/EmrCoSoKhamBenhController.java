package vn.ehealth.emr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.EmrCoSoKhamBenh;
import vn.ehealth.emr.service.EmrCoSoKhamBenhService;

@RestController
@RequestMapping("/api/coso_khambenh")
public class EmrCoSoKhamBenhController {
    
    @Autowired EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    
    @GetMapping("/get_all_cskb")
    public List<EmrCoSoKhamBenh> getAllCoSoKhamBenh() {
        return emrCoSoKhamBenhService.getAll();        
    }

}
