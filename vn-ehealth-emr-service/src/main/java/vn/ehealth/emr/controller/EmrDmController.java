package vn.ehealth.emr.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import vn.ehealth.emr.model.EmrDm;
import vn.ehealth.emr.service.EmrDmService;

@RestController
@RequestMapping("/api/danhmuc")
public class EmrDmController {
    
    private Logger logger = LoggerFactory.getLogger(EmrDmController.class);
    
    @Autowired EmrDmService emrDmService;

    @GetMapping("/count_dm_list")
    public long countDmList(@RequestParam("dm_type") String dmType, 
                            @RequestParam Optional<String> keyword,
                            @RequestParam Optional<Integer> level,
                            @RequestParam Optional<String> parentCode) {
        return emrDmService.countEmrDm(dmType, keyword.orElse(""), 
                                        level.orElse(-1), parentCode.orElse(""));
    }
    
    @GetMapping("/get_dm_list")
    public ResponseEntity<?> getDmList(@RequestParam("dm_type") String dmType, 
                                        @RequestParam Optional<String> keyword,
                                        @RequestParam Optional<Integer> level,
                                        @RequestParam Optional<String> parentCode,
                                        @RequestParam Optional<Integer> start, 
                                        @RequestParam Optional<Integer> count) {
        var lst = emrDmService.getEmrDmList(dmType, keyword.orElse(""), 
                                level.orElse(-1), parentCode.orElse(""), 
                                start.orElse(-1), count.orElse(-1));
        return ResponseEntity.ok(lst);
    }
    
    @GetMapping("/get_all_dm_list")
    public ResponseEntity<?> getAllDmList(@RequestParam("dm_type") String dmType) {
        var lst = emrDmService.getAllEmrDm(dmType);
        return ResponseEntity.ok(lst);
    }
    
    @GetMapping("/export_dm_list")
    public ResponseEntity<?> exportDmList(@RequestParam("dm_type") String dmType) {
        var builder = new StringBuilder("STT;Mã;Tên\n");
        var lst = emrDmService.getAllEmrDm(dmType);
        for(int i = 0; i < lst.size(); i++) {
            var item = lst.get(i);
            builder.append((i+1)).append(";")
                    .append(item.ma).append(";")
                    .append(item.ten).append("\n");
        }
        
        var data = builder.toString().getBytes();
        return ResponseEntity.ok()
                .contentLength(data.length)
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-disposition", "attachment; filename=" + dmType + ".csv")
                .body(new ByteArrayResource(data));
        
    }
    
    @PostMapping("/import_dm_list")
    public ResponseEntity<?> importDmList(@RequestParam("dm_type") String dmType, @RequestParam("csv_file") MultipartFile csvFile) {

        try {
            var reader = new CSVReader(new InputStreamReader(csvFile.getInputStream()), ';');
            String csv = new String(csvFile.getBytes());
            
            var emrDmList = new ArrayList<EmrDm>();
            String[] line = reader.readNext();
            while ((line = reader.readNext()) != null) {
                var emrDm = new EmrDm();
                emrDm.ma = line[1];
                emrDm.ten = line[2];
                emrDmList.add(emrDm);
            }
            reader.close();
            emrDmService.importEmrDmList(dmType, emrDmList);
            
            return ResponseEntity.ok(Map.of("success", true, "csv", csv));
        } catch (Exception e) {
            logger.error("Fail to upload file:", e);
            return new ResponseEntity<>(Map.of("success", false, "error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
