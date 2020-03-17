package vn.ehealth.emr.service;

import java.util.Optional;

import org.hl7.fhir.r4.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ehealth.emr.repository.LocationRepository;
import vn.ehealth.hl7.fhir.provider.entity.LocationEntity;

@Service
public class LocationService extends ResourceService<LocationEntity, Location>{

    @Autowired private LocationRepository locationRepository;
    
    public Optional<LocationEntity> getByIdentifier(String identifier) {
        return locationRepository.findByIdentifierValueAndActive(identifier, true);
    }

    @Override
    LocationEntity fromFhir(Location obj) {
        return LocationEntity.fromLocation(obj);
    }

    @Override
    Location toFhir(LocationEntity ent) {
        return LocationEntity.toLocation(ent);
    }
}
