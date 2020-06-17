package vn.ehealth.cdr.controller;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hatt")
public class HinhAnhTonThuongController {
  
    @GetMapping("/get_ds_hatt")
    public ResponseEntity<?> getDsHatt(@RequestParam("hsba_id") String hsbaId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
}
