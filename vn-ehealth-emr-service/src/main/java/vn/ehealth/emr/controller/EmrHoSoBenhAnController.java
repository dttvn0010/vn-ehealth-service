package vn.ehealth.emr.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.sf.jasperreports.engine.JRException;
import vn.ehealth.emr.model.EmrFileDinhKem;
//import vn.ehealth.emr.cda.CDAExportUtil;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.service.EmrBenhNhanService;
import vn.ehealth.emr.service.EmrCoSoKhamBenhService;
import vn.ehealth.emr.service.EmrHoSoBenhAnService;
import vn.ehealth.emr.service.EmrVaoKhoaService;
import vn.ehealth.emr.service.UserService;
import vn.ehealth.emr.utils.DateUtil;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.PDFExportUtil;
import vn.ehealth.emr.utils.UserUtil;
import vn.ehealth.emr.utils.Constants.TRANGTHAI_HOSO;
import vn.ehealth.emr.validate.ErrorMessage;
import vn.ehealth.emr.validate.JsonParser;

import java.io.*;

@RestController
@RequestMapping("/api/hsba")
public class EmrHoSoBenhAnController {
    
    @Value("${server.upload.path}")
    private String uploadPath;
    
    private static Logger logger = LoggerFactory.getLogger(EmrHoSoBenhAnController.class);
    
    private JsonParser jsonParser = new JsonParser();
    
    private static String hsbaSchema = "";
    
    private static Properties fieldsConvertProp = new Properties();
    
    
    static {
        try {
            hsbaSchema = new String(new ClassPathResource("static/json/hsba_schema.json").getInputStream().readAllBytes());
        } catch (IOException e) {
            logger.error("Cannot read hsba schema", e);
        }
        
        try {
            fieldsConvertProp.load(new ClassPathResource("fields_convert.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Cannot read fieldsConvert properties", e);
        }
        
    }
    
    @Autowired EmrHoSoBenhAnService emrHoSoBenhAnService;    
    @Autowired EmrBenhNhanService emrBenhNhanService;
    @Autowired EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    @Autowired EmrVaoKhoaService emrVaoKhoaService;    
    @Autowired UserService userService;

    @GetMapping("/count_ds_hs")
    public ResponseEntity<?> countHsba(@RequestParam int trangthai, @RequestParam String mayte) {
        try {
            var user = UserUtil.getCurrentUser();
            var count = emrHoSoBenhAnService.countHoSo(user.get().emrCoSoKhamBenhId, trangthai, mayte);
            return ResponseEntity.ok(count);
        }catch (Exception e) {
            logger.error("Error countHsba:", e);
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }        
    }
    
    @GetMapping("/get_ds_hs_by_bn")
    public ResponseEntity<?> getDsHsbaByBenhNhan(@RequestParam("benhnhan_id") String benhNhanId) {
        var result = emrHoSoBenhAnService.getByEmrBenhNhanId(new ObjectId(benhNhanId));
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/get_ds_hs")
    public ResponseEntity<?> getDsHsba(@RequestParam int trangthai ,
                                                @RequestParam String mayte,
                                                @RequestParam int start, 
                                                @RequestParam int count) {
        
        try {
            var user = UserUtil.getCurrentUser();
            var result = emrHoSoBenhAnService.getDsHoSo(user.get().emrCoSoKhamBenhId, trangthai, mayte, start, count);
            return ResponseEntity.ok(result);
            
        }catch(Exception e) {
            logger.error("Error getDsHsba:", e);
            return new ResponseEntity<>(new ArrayList<EmrHoSoBenhAn>(), HttpStatus.BAD_REQUEST);
        }        
    }
    
    @GetMapping("/get_hsba_logs")
    public ResponseEntity<?> getHsbaLogs(@RequestParam("hsba_id") String id) {        
        return ResponseEntity.ok(emrHoSoBenhAnService.getHistory(new ObjectId(id)));
    }    
    
    
    @GetMapping("/get_hs_goc")
    public ResponseEntity<?> getHsGoc(@RequestParam("hsba_id") String id) {
        return ResponseEntity.ok(Map.of("hsGoc", emrHoSoBenhAnService.getHsgoc(new ObjectId(id))));
    }
    
    
    @GetMapping("/get_hsba_by_id")
    public ResponseEntity<?> getHsbaById(@RequestParam("hsba_id") String id) {        
        var hsba = emrHoSoBenhAnService.getById(new ObjectId(id));
        return ResponseEntity.of(hsba);
    }
    
    @GetMapping("/archive_hsba")
    public ResponseEntity<?> archiveHsba(@RequestParam("hsba_id") String id) {
        
        try {
            var user = UserUtil.getCurrentUser();
            emrHoSoBenhAnService.archiveHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(Map.of("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(Map.of("success", false, "error", error));
        }
    }
    
    @GetMapping("/unarchive_hsba")
    public ResponseEntity<?> unArchiveHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();            
            emrHoSoBenhAnService.unArchiveHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(Map.of("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(Map.of("success", false, "error", error));
        }
    }
    
    @GetMapping("/delete_hsba")
    public ResponseEntity<?> deleteHsba(@RequestParam("hsba_id") String id) {
        try {
            var user = UserUtil.getCurrentUser();
            emrHoSoBenhAnService.deleteHsba(new ObjectId(id), user.get().id);
            return ResponseEntity.ok(Map.of("success", true));
        }catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            return ResponseEntity.ok(Map.of("success", false, "error", error));
        }
    }
    
    @GetMapping("/download_cda")
    public ResponseEntity<?> downloadCDA(@RequestParam("hsba_id") String id) {
        /*
        var hsbaOpt = emrHoSoBenhAnService.getById(new ObjectId(id));
        
        if(hsbaOpt.isPresent()) {
            try {
                var hsba = hsbaOpt.get();
                hsba.getEmrBenhNhan();
                hsba.getEmrCoSoKhamBenh();
                emrHoSoBenhAnService.getEmrHoSoBenhAnDetail(hsba);
                var data = new byte[0];// CDAExportUtil.exportCDA(hsba);
                var resource = new ByteArrayResource(data);
                
                return ResponseEntity.ok()
                        .contentLength(data.length)
                        .contentType(MediaType.parseMediaType("application/xml"))
                        .header("Content-disposition", "attachment; filename=cda_" + id + ".xml")
                        .body(resource);
            }catch(Exception e) {
                logger.error("Error exporting pdf :", e);
            }
        }*/
        
        return ResponseEntity.badRequest().build();
    }
     
    @GetMapping("/view_pdf")
    public ResponseEntity<?> viewPdf(@RequestParam("hsba_id") String id) {
        
        var hsba = emrHoSoBenhAnService.getById(new ObjectId(id));
        
        if(hsba.isPresent()) {
            try {
                emrHoSoBenhAnService.getEmrHoSoBenhAnDetail(hsba.get());
                var data = PDFExportUtil.exportPdf(hsba.get(), "");
                var resource = new ByteArrayResource(data);
                
                return ResponseEntity.ok()
                        .contentLength(data.length)
                        .contentType(MediaType.parseMediaType("application/pdf"))
                        .body(resource);
            }catch(JRException | IOException  | NullPointerException e) {
                logger.error("Error exporting pdf :", e);
            }
        }
        
        return ResponseEntity.badRequest().build();
    }
    
    @PostMapping("/update_hsba")
    public ResponseEntity<?> updateHsba(@RequestBody String jsonSt) {
        var errors = new ArrayList<ErrorMessage>();
        var objMap = jsonParser.parseJson(jsonSt, hsbaSchema, errors);
        
        if(errors.size() > 0) {
            var result = Map.of(
                "success" , false,
                "errors", errors 
            );
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        try {
            var user = UserUtil.getCurrentUser();
            var mapper = EmrUtils.createObjectMapper();
            var hsba = mapper.convertValue(objMap, EmrHoSoBenhAn.class);
            hsba = emrHoSoBenhAnService.update(hsba, user.get().id);          
            
            var result = Map.of(
                "success" , true,
                "emrHoSoBenhAn", hsba 
            );
                        
            return ResponseEntity.ok(result);            
        }catch(Exception e) {
            var result = Map.of(
                "success" , false,
                "errors", List.of(e.getMessage()) 
            );
            logger.error("Error save hsba:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
    }
    
    private String preprocessJsonFields(String jsonSt) {
        for(var entry: fieldsConvertProp.entrySet()) {
            String field = (String) entry.getKey();
            String fieldReplace = (String) entry.getValue();
            jsonSt = jsonSt.replace("\"" + field + "\"", "\"" + fieldReplace + "\"");
        }
        return jsonSt;
    }
    
    @PostMapping("/create_or_update_hsba")
    public ResponseEntity<?> createOrUpdateHsbaHIS(@RequestBody String jsonSt) {        
        
        jsonSt = preprocessJsonFields(jsonSt);
        var errors = new ArrayList<ErrorMessage>();
        var objMap = jsonParser.parseJson(jsonSt, hsbaSchema, errors);
        
        if(errors.size() > 0) {
            var result = Map.of(
                "success" , false,
                "errors", errors 
            );
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        
        try {
            var user = UserUtil.getCurrentUser();
            var userId = user.map(x -> x.id).orElse(null);
            var mapper = EmrUtils.createObjectMapper();
            var hsba = mapper.convertValue(objMap, EmrHoSoBenhAn.class);
            emrHoSoBenhAnService.createOrUpdateHIS(hsba, userId, jsonSt);
                        
            var result = Map.of(
                "success" , true,
                "emrHoSoBenhAn", hsba  
            );
            
            return ResponseEntity.ok(result);
        
        } catch(Exception e) {
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of(
                "success" , false,
                "errors", List.of(error) 
            );
            logger.error("Error save hsba:", e);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/report")
    public ResponseEntity<?> getReportHsba(String maLoaiBenhAn, String maCoSoKhamBenh, Optional<String> startDate, Optional<String> endDate) {
        var fromDate = startDate.map(x -> DateUtil.parseStringToDate(x, DateUtil.FORMAT_DD_MM_YYYY_HH_MM)).orElse(null);
        var toDate = endDate.map(x -> DateUtil.parseStringToDate(x, DateUtil.FORMAT_DD_MM_YYYY_HH_MM)).orElse(null);
        var emrCoSoKhamBenhId = emrCoSoKhamBenhService.getByMa(maCoSoKhamBenh).map(x -> x.id).orElse(null);
        var hsbaList = emrHoSoBenhAnService.getReport(emrCoSoKhamBenhId, maLoaiBenhAn, fromDate, toDate);
        var groups = new HashMap<>();
        
        for(var hsba : hsbaList) {
            if(!groups.containsKey(hsba.emrDmLoaiBenhAn.ma)) {
                groups.put(hsba.emrDmLoaiBenhAn.ma, new int[2]);
            }
            var group = (int[]) groups.get(hsba.emrDmLoaiBenhAn.ma);
            if(hsba.trangThai == TRANGTHAI_HOSO.CHUA_XULY) {
                group[0] += 1;
            }else if(hsba.trangThai == TRANGTHAI_HOSO.DA_LUU) {
                group[1] += 1;
            }
        }
        
        return ResponseEntity.ok(groups);
        
    }
    
    private String modifyFilename(String filename) {
        long time = System.currentTimeMillis();
        int pos = filename.lastIndexOf('.');
        if(pos >= 0) {
            return filename.substring(0, pos) + "_" + String.valueOf(time) + filename.substring(pos);
        }else {
            return filename + "_" + String.valueOf(time);
        }        
    }
    
    @PostMapping("/add_giayto")
    public ResponseEntity<?> addGiayToDinhKem(@RequestParam("hsba_id") String id, @RequestParam("attached_files") MultipartFile[] attachedFiles) {
        try {
            var emrFileDinhKems = new ArrayList<EmrFileDinhKem>();
            
            for(var attachedFile : attachedFiles) {
                var filename = modifyFilename(attachedFile.getOriginalFilename());
                var file = new File(uploadPath + "/" + filename);
                attachedFile.transferTo(file);
                
                var emrFileDinhKem = new EmrFileDinhKem();
                emrFileDinhKem.url = "/upload/" + filename;
                emrFileDinhKem.name = attachedFile.getOriginalFilename();
                emrFileDinhKems.add(emrFileDinhKem);
            }
            
            emrHoSoBenhAnService.addEmrFileDinhKems(new ObjectId(id), emrFileDinhKems);
            return ResponseEntity.ok(Map.of("success", true));
        }catch(Exception e) {
            Log.error("Fail to upload giayto:", e);
            return new ResponseEntity<>(Map.of("success", false, "error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}