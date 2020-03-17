package vn.ehealth.emr.entity.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.service.ResourceService;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public abstract class BaseController<ENT extends BaseResource> {
    
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    
    protected ObjectMapper objectMapper = new ObjectMapper();
            
    abstract protected ResourceService<ENT> getEntService(); 
    
    @GetMapping("/get_by_id/{fhirId}")
    public ResponseEntity<?> getById(@PathVariable("fhirId") String fhirId) {
        var ent = getEntService().getByFhirId(fhirId).get();
        return ResponseEntity.ok(ent);
    }
    
    @GetMapping("/get_all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(getEntService().getAll());
    }
        
    @PostMapping("/create_or_update")
    public ResponseEntity<?> createOrUpdate(@RequestBody ENT ent) {
        try {
            ent = getEntService().save(ent);
            var result = Map.of("success", true, "entity", ent);
            return ResponseEntity.ok(result);
        }catch(Exception e) {
            logger.error("Can not save entity: ", e);
            var error = Optional.ofNullable(e.getMessage()).orElse("Unknown error");
            var result = Map.of("success", false, "error", error);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }
}
