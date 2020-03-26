package vn.ehealth.hl7.fhir.core.util;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.StringType;

public class FhirUtil {

    public static CodeableConcept findConceptBySystem(List<CodeableConcept> lst, @Nonnull String system) {
        if(lst == null) return null;
        
        for(var concept : lst) {
            if(FPUtil.anyMatch(concept.getCoding(), x -> system.equals(x.getSystem()))) {
                return concept;
            };
        }
        return null;
    }
    
    public static Extension findExtensionByURL(List<Extension> lst, @Nonnull String url) {
        return FPUtil.findFirst(lst, x -> url.equals(x.getUrl()));
    }
    
    public static Identifier createIdentifier(String value, String system) {
        var identifier = new Identifier();
        identifier.setValue(value);
        identifier.setSystem(system);
        return identifier;
    }
    
    public static Period createPeriod(Date start, Date end) {
        var period = new Period();
        period.setStart(start);
        period.setEnd(end);
        return period;
    }
    
    public static Identifier createIdentifier(String value, String system, Date start, Date end) {
        var identifier = new Identifier();
        identifier.setValue(value);
        identifier.setSystem(system);
        identifier.setPeriod(createPeriod(start, end));
        return identifier;
    }
    
    public static Address createAddress(String text) {
        if(text == null) return null;
        var address = new Address();
        address.setText(text);
        return address;
    }
    
    public static HumanName createHumanName(String text) {
        if(text == null) return null;
        var name = new HumanName();
        name.setText(text);
        return name;
    }
    
    
    public static ContactPoint createContactPoint(String value, ContactPointSystem system) {
        var contactPoint = new ContactPoint();
        contactPoint.setValue(value);
        contactPoint.setSystem(system);
        return contactPoint;
    }
    
    public static CodeableConcept createCodeableConcept(String text) {
        if(text == null) return null;
        var concept = new CodeableConcept();
        concept.setText(text);
        return concept;
    }
    
    public static Annotation createAnnotation(String text) {
        if(text == null) return null;
        var annotation = new Annotation();
        annotation.setText(text);
        return annotation;
    }
    
    public static Extension createExtension(String url, String text) {
        if(text == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(new StringType(text));
        return extension;
    }
    
    public static Extension createExtension(String url, CodeableConcept concept) {
        if(concept == null) return null;
        var extension = new Extension();
        extension.setUrl(url);
        extension.setValue(concept);
        return extension;
    }
    
    public static CodeableConcept createCodeableConcept(String code, String name, String system) {
        var concept = new CodeableConcept();
        concept.setText(name);
        var coding = new Coding();
        coding.setCode(code);
        coding.setDisplay(name);
        coding.setSystem(system);
        concept.addCoding(coding);
        return concept;
    }
    
    public static Reference createReference(Resource resource) {
        if(resource == null) return null;
        return new Reference(resource.getResourceType() + "/" + resource.getId());
    }
    
    public static Reference createReference(ResourceType type, String id) {
        if(type == null || id == null) return null;
        return new Reference(type + "/" + id);
    }
    
    public static IdType createIdType(String id) {
        return new IdType(id);
    }
    
    public static IdType idTypeFromRef(Reference ref) {
        var id = idFromRef(ref);
        return id != null? new IdType(id) : null;
    }
    
    public static String idFromRef(Reference ref) {
        if((ref != null && ref.hasReference())) {
            var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return arr[1];
            }
        }
        return null;
    }
    
    public static ResourceType getResourceType(Reference ref) {
    	if(ref != null && ref.hasReference()) {
    		var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return ResourceType.fromCode(arr[0]);
            }
    	}
    	return null;
    }
    
    public static IdType createIdType(Reference ref) {
        if((ref != null && ref.hasReference())) {
            var arr = ref.getReference().split("/");
            if(arr.length == 2) {
                return new IdType(arr[1]);
            }
        }
        return null;
    }
    
    public static boolean conceptHasCode(CodeableConcept concept, String code, String system) {
        if(concept != null && concept.hasCoding()) {
            return FPUtil.anyMatch(concept.getCoding(), 
                    x -> code.equals(x.getCode()) && system.equals(x.getSystem()));
        }
        return false;
    }
}
