package vn.ehealth.emr.model.dto;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static vn.ehealth.hl7.fhir.core.util.FhirUtil.*;

public class BaseRef {

	public String id;
	public String display;
	public BaseModelDTO data;
	
	@JsonIgnore
	public IBaseResource resource;
	
	@JsonIgnore
	public ResourceType resourceType;
	
	public BaseRef() {
		
	}
	
	public BaseRef(String display) {
	    this.display = display;
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
			this.display = ref.getDisplay();
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
	
	public static Reference toRef(ResourceType resourceType, BaseRef dto) {
	    if(dto != null) {
    	    if(dto.id != null) {
    	        return createReference(resourceType, dto.id);
    	    }else if(dto.display != null) {
    	        return createReference(dto.display);
    	    }
	    }	    
	    return null;
	}
	
	public static BaseRef fromPractitionerRef(Reference ref) {
	    if(ref == null) return null;
	    var dto = new BaseRef(ref);
	    dto.data = CanboYte.fromFhir((Practitioner) ref.getResource());
	    return dto;
	}
	
	public static Reference toPractitionerRef(BaseRef dto) {
	    return toRef(ResourceType.Practitioner, dto);
	}
	
	public static Reference toPatientRef(BaseRef dto) {
	    return toRef(ResourceType.Patient, dto);
	}
	
	public static BaseRef fromPatientRef(Reference ref) {
	    if(ref == null) return null;
        var dto = new BaseRef(ref);
        dto.data = BenhNhan.fromFhir((Patient) ref.getResource());
        return dto;
	}
	
	public static Reference toOrganizationRef(BaseRef dto) {
        return toRef(ResourceType.Organization, dto);
    }
	
	public static BaseRef fromServiceProviderRef(Reference ref) {
        if(ref == null) return null;
        var dto = new BaseRef(ref);
        dto.data = CoSoKhamBenh.fromFhir((Organization) ref.getResource());
        return dto;
    }
	
	public static BaseRef fromFalcultyRef(Reference ref) {
        if(ref == null) return null;
        var dto = new BaseRef(ref);
        dto.data = KhoaDieuTri.fromFhir((Organization) ref.getResource());
        return dto;
    }
	
	public static Reference toEncounterRef(BaseRef dto) {
        return toRef(ResourceType.Encounter, dto);
    }
	
	public static BaseRef fromHsbaEncounterRef(Reference ref) {
	    if(ref == null) return null;
        var dto = new BaseRef(ref);
        dto.data = DotKhamBenh.fromFhir((Encounter) ref.getResource());
        return dto;
    }
	
	public static BaseRef fromVaoKhoaEncounterRef(Reference ref) {
        if(ref == null) return null;
        var dto = new BaseRef(ref);
        dto.data = VaoKhoa.fromFhir((Encounter) ref.getResource());
        return dto;
    }
}
