package vn.ehealth.emr.dto.controller;

import java.util.Map;
import java.util.Optional;

import org.hl7.fhir.r4.model.IdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.model.dto.DotKhamBenh;
import vn.ehealth.emr.utils.JsonUtil;

import vn.ehealth.hl7.fhir.ehr.dao.impl.EpisodeOfCareDao;
import vn.ehealth.hl7.fhir.provider.dao.impl.OrganizationDao;

import static vn.ehealth.hl7.fhir.core.util.DataConvertUtil.*;
import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

@RestController
@RequestMapping("/api/dot_kham_benh")
public class DotKhamBenhController {

    private static Logger logger = LoggerFactory.getLogger(BenhNhanController.class);
    
    @Autowired private EpisodeOfCareDao episodeOfCareDao;
    @Autowired private OrganizationDao organizationDao;
        
    private Map<String, Object> convertToRaw(DotKhamBenh dto) {
    	if(dto == null) return null;
    	
    	var item = JsonUtil.objectToMap(dto);
    	var serviceProvider = organizationDao.read(createIdType(dto.serviceProviderId));
    	if(serviceProvider != null) {
    		item.put("coSoKhamBenh", serviceProvider.getName());
    	}
    	return item;
    }
    
    @GetMapping("/get_by_id")
    public ResponseEntity<?> getById(@RequestParam String id) {
        var obj = episodeOfCareDao.read(new IdType(id));
        var dto = DotKhamBenh.fromFhir(obj);
        return ResponseEntity.ok(convertToRaw(dto));
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        var lst = transform(episodeOfCareDao.getAll(), x -> DotKhamBenh.fromFhir(x));
        var result = transform(lst, x -> convertToRaw(x));
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody DotKhamBenh dto) {
        try {
            var obj = DotKhamBenh.toFhir(dto);            
            
            if(obj.hasId()) {
                obj = episodeOfCareDao.update(obj, obj.getIdElement());
            }else {
                obj = episodeOfCareDao.create(obj);
            }
            dto = DotKhamBenh.fromFhir(obj);
            var result = mapOf("success", true, "dto", dto);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = mapOf("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
