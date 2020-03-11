package vn.ehealth.hl7.fhir.user.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Person;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.user.entity.PersonEntity;

@Component
public class PersonEntityToFHIRPerson implements Transformer<PersonEntity, Person> {
    @Override
    public Person transform(PersonEntity ent) {
        var obj = PersonEntity.toPerson(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Person-v1.0"));
        obj.setExtension(ent.extension);
        obj.setId(ent.fhir_id);
        return obj;        
    }
}
