package vn.ehealth.hl7.fhir.core.entity;

import org.hl7.fhir.r4.model.Location.LocationPositionComponent;

public class BaseGeoLocation {

    public Double longitude;
    public Double latitude;
    public Double altitude;
   
    public static BaseGeoLocation fromLocationPositionComponent(LocationPositionComponent object) {
        if(object == null) return null;
        
        var entity = new BaseGeoLocation();
                
        if(object.getLongitude() != null)
            entity.longitude = object.getLongitude().doubleValue();
        
        if(object.getLatitude() != null)
            entity.latitude = object.getLatitude().doubleValue();
        
        if(object.getAltitude() != null)
            entity.altitude = object.getAltitude().doubleValue();
       
        return null;
    }
    
    public static LocationPositionComponent toLocationPositionComponent(BaseGeoLocation entity) {
        if(entity == null) return null;
        
        var object = new LocationPositionComponent();
        if(entity.longitude != null) {
            object.setLongitude(entity.longitude.doubleValue());
        }
        if(entity.latitude != null) {
            object.setLatitude(entity.latitude.doubleValue());
        }
        if(entity.altitude != null) {
            object.setAltitude(entity.altitude.doubleValue());
        }
        return object;
    }

}
