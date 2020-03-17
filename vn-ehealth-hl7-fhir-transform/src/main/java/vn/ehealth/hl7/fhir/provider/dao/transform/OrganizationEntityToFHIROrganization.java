package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.OrganizationEntity;

@Component
public class OrganizationEntityToFHIROrganization implements Transformer<OrganizationEntity, Organization> {
    @Override
    public Organization transform(OrganizationEntity ent) {
        var obj = OrganizationEntity.toOrganization(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Organization-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhirId);
        return obj;
    }
}
