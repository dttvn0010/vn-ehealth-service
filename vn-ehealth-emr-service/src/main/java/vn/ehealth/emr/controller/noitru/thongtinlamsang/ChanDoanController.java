package vn.ehealth.emr.controller.noitru.thongtinlamsang;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;

@RestController
@RequestMapping("/api/noitru/thongtin_lamsang/chandoan")
public class ChanDoanController {
	
	
	@PostMapping("add_chan_doan")
	public ResponseEntity<?> addChanDoan(@RequestBody String json) {
		
		return ResponseEntity.ok(mapOf("success", true));
	}
}
