package vn.ehealth.emr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.service.EmrCoSoKhamBenhService;

@RestController
@RequestMapping("/api/coso_khambenh")
public class EmrCoSoKhamBenhController {
    
    @Autowired EmrCoSoKhamBenhService emrCoSoKhamBenhService;
}
