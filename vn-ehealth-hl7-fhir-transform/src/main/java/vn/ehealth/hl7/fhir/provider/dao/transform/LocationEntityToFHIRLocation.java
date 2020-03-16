package vn.ehealth.hl7.fhir.provider.dao.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.r4.model.Location;
import org.springframework.stereotype.Component;

import vn.ehealth.hl7.fhir.core.util.DataConvertUtil;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

@Component
public class LocationEntityToFHIRLocation implements Transformer<LocationEntity, Location> {
    @Override
    public Location transform(LocationEntity ent) {
        var obj = LocationEntity.toLocation(ent);
        obj.setMeta(DataConvertUtil.getMeta(ent, "Location-v1.0"));
        DataConvertUtil.getMetaExt(ent, obj);
        obj.setId(ent.fhir_id);
        return obj;
    }
}
