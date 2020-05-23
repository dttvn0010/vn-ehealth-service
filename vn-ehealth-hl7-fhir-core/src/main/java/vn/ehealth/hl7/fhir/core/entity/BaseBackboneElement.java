package vn.ehealth.hl7.fhir.core.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Transient;

public class BaseBackboneElement {

	public List<BaseExtension> modifierExtension;
	
	@Transient
    public Map<String, Object> computes = new HashMap<>();
    
}
