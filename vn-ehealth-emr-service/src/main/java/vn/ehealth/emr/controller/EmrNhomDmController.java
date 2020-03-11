package vn.ehealth.emr.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.EmrNhomDm;
import vn.ehealth.emr.service.EmrNhomDmService;

@RestController
@RequestMapping("/api/nhom_danhmuc")
public class EmrNhomDmController {

    
    @Autowired EmrNhomDmService emrNhomDmService;
    
    @GetMapping("/count_nhom_dm_list")
    public long count(@RequestParam String keyword) {
        return emrNhomDmService.countEmrNhomDm(keyword);        
    }
    
    @GetMapping("/get_nhom_dm_list")
    public List<EmrNhomDm> getNhomDmList(@RequestParam String keyword, 
                            @RequestParam Optional<Integer> start, 
                            @RequestParam Optional<Integer> count) {
        return emrNhomDmService.getEmrNhomDmList(keyword, start, count);
    }
    
    @GetMapping("/get_ten_nhom_dm")
    public ResponseEntity<?> getTenNhomDm(@RequestParam String ma) {
        var emrNhomDm = emrNhomDmService.getByMa(ma);
        return ResponseEntity.of(emrNhomDm.map(x -> x.ten));
    }
}
