package vn.ehealth.hl7.fhir.term.entity;

public class ConceptReferenceEntity {

    public String code;
    public String display;
    
    public ConceptReferenceEntity() {
        
    }
    
    public ConceptReferenceEntity(String code, String display) {
        this.code = code;
        this.display = display;
    }
}
