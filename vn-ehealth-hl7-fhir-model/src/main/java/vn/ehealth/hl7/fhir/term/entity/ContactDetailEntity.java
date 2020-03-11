package vn.ehealth.hl7.fhir.term.entity;

import java.util.List;

import org.hl7.fhir.r4.model.ContactDetail;

import vn.ehealth.hl7.fhir.core.entity.BaseContactPoint;

public class ContactDetailEntity {
    public String name;
    public List<BaseContactPoint> telecom;
    
    public static ContactDetailEntity fromContactDetail(ContactDetail obj) {
        if(obj == null) return null;
        var ent = new ContactDetailEntity();
        ent.name = obj.getName();
        ent.telecom = BaseContactPoint.fromContactPointList(obj.getTelecom());
        return ent;
    }
    
    public static ContactDetail toContactDetail(ContactDetailEntity ent) {
        if(ent == null) return null;
        var obj = new ContactDetail();
        obj.setName(ent.name);
        obj.setTelecom(BaseContactPoint.toContactPointList(ent.telecom));
        return obj;
    }
}
