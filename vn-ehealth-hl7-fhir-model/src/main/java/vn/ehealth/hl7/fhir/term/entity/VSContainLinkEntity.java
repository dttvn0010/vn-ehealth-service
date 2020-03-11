package vn.ehealth.hl7.fhir.term.entity;

import org.hl7.fhir.r4.model.CodeSystem.CodeSystemHierarchyMeaning;



/**
 * @author SONVT24
 * @since 2019
 * @version 1.0
 * */
public class VSContainLinkEntity {
    public CodeSystemHierarchyMeaning relationship;
    public ValueSetContainEntity childContain;
}
