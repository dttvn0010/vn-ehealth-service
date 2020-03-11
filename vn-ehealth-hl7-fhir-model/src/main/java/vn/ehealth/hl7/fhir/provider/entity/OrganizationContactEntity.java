package vn.ehealth.hl7.fhir.provider.entity;

import java.util.List;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Organization.OrganizationContactComponent;

import vn.ehealth.hl7.fhir.core.entity.BaseAddress;
import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;
import vn.ehealth.hl7.fhir.core.entity.BaseHumanName;

public class OrganizationContactEntity {

    public CodeableConcept purpose;
    public BaseHumanName name;
    public List<BaseContactPoint> telecom;
    public BaseAddress address;
    
    public static OrganizationContactEntity fromOrganizationContactComponent(OrganizationContactComponent obj ) {
        if(obj == null) return null;
        var ent = new OrganizationContactEntity();
        ent.purpose = obj.getPurpose();
        ent.name = BaseHumanName.fromHumanName(obj.getName());
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        ent.address = BaseAddress.fromAddress(obj.getAddress());
        return ent;
    }
    
    public static OrganizationContactComponent toOrganizationContactComponent(OrganizationContactEntity ent) {
        if(ent == null) return null;
        
        var obj = new OrganizationContactComponent();
        obj.setPurpose(ent.purpose);
        obj.setName(BaseHumanName.toHumanName(ent.name));
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        obj.setAddress(BaseAddress.toAddress(ent.address));
        return obj;
    }
}
