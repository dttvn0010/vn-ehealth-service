package vn.ehealth.emr.model.dto;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class BaseRef {

	public String id;
	public BaseModelDTO data;
	
	@JsonIgnore
	public IBaseResource resource;
	
	@JsonIgnore
	public ResourceType resourceType;
	
	public BaseRef() {
		
	}
	
	public BaseRef(ResourceType resourceType, String id) {
		this.resourceType = resourceType;
		this.id = id;
	}
	
	public BaseRef(Resource obj) {
		if(obj != null) {
			this.resourceType = obj.getResourceType();
			this.id = obj.getId();
		}
	}
	
	public BaseRef(Reference ref) {
		if(ref != null) {
			this.id = idFromRef(ref);
			this.resourceType = getResourceType(ref);
			this.resource = ref.getResource();
		}
	}
	
	public static IdType toIdType(BaseRef dto) {
		if(dto != null && dto.id != null) {
			return new IdType(dto.id);
		}
		return null;
	}
}
