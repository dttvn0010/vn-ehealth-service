package vn.ehealth.cdr.controller;

import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hoichan")
public class HoiChanController {
    
    @GetMapping("/get_ds_hoichan")
    public ResponseEntity<?> getDsHoichan(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }
    
}
