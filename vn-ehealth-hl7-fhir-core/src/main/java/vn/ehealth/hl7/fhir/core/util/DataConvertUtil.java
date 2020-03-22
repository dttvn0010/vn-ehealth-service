package vn.ehealth.hl7.fhir.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Meta;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class DataConvertUtil {
    
    @SafeVarargs
    public static<T> List<T> listOf(T...arr) {
        var lst = new ArrayList<T>();
        for(T obj: arr) {
            if(obj != null) lst.add(obj);
        }
        return lst;
    }
    
    public static  Map<String, String> mapOf(String ...obj) {
        var m = new HashMap<String, String>();
        if(obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for(int i = 0; i < n; i++) {
            String key = obj[2*i];
            String value = obj[2*i + 1];
            if(key != null && value != null) {
                m.put(key, value);
            }
        }
        return m;
    }
    
    public static Map<String, Object> mapOf(Object ...obj) {
        var m = new HashMap<String, Object>();
        if(obj.length % 2 != 0) {
            throw new RuntimeException("Number of map items must be even");
        }
        int n = obj.length / 2;
        for(int i = 0; i < n; i++) {
            Object key = obj[2*i];
            Object value = obj[2*i + 1];
            if(key != null && value != null) {
                m.put(key.toString(), value);
            }
        }
        return m;
    }
    
    public static <T> Map<String, Map<String, T>> mapOf3(String keyLevel1, String keyLevel2, T value) {
        if(value != null) {
            return Map.of(keyLevel1, Map.of(keyLevel2, value));
        }
        return new HashMap<>();
    }
    
    public static <T, U> List<U> transform(List<T> lst, Function<T, U> func) {
        if(lst != null) {
            return lst.stream()
                      .filter(x -> x != null) 
                      .map(x -> func.apply(x))
                      .collect(Collectors.toList());
        }
        return null;
    }
   
    public static Meta getMeta(BaseResource entity, String profile) {
        if(entity == null) return null;
        
        var meta = new Meta();
        if(entity.profile != null && entity.profile.size() > 0) {
            meta.setProfile(transform(entity.profile, x -> new CanonicalType(x)));
        }else {
            meta = new Meta().addProfile(ConstantKeys.ENTITY_PROFILE_V1 + profile);
        }
        
        meta.setSecurity(BaseCoding.toCodingList(entity.security));
        meta.setTag(BaseCoding.toCodingList(entity.tag));
        
        if(entity.resDeleted != null) {
            meta.setLastUpdated(entity.resDeleted);
        }else if(entity.resUpdated != null) {
            meta.setLastUpdated(entity.resUpdated);
        }else if(entity.resCreated != null) {
            meta.setLastUpdated(entity.resCreated);
        }
        if(entity.version != null) {
            meta.setVersionId(String.valueOf(entity.version));
        }
        
        return meta;
    }
    
    public static void setMetaExt(DomainResource obj, BaseResource ent) {
        if(obj != null && ent != null) {
            if(obj.hasMeta()) {
                if (obj.getMeta().hasProfile()) {
                    ent.profile = transform(obj.getMeta().getProfile() , x -> x.getValue());
                }
                if (obj.getMeta().hasSecurity()) {
                    ent.security = BaseCoding.fromCodingList(obj.getMeta().getSecurity());
                }
                if (obj.getMeta().hasTag()) {
                    ent.tag = BaseCoding.fromCodingList(obj.getMeta().getTag());
                }
            }
            
            ent.extension = obj.hasExtension()? obj.getExtension() : null;
            ent.modifierExtension = obj.hasModifierExtension()? obj.getModifierExtension(): null;
        }
    }
    
    public static void getMetaExt(BaseResource ent, DomainResource obj) {
        if(obj != null && ent != null) {
        	if (ent.profile != null && ent.profile.size() > 0) {
        		obj.getMeta().setProfile(transform(ent.profile, x -> new CanonicalType(x)));
        	}
        	if (ent.tag != null && ent.tag.size() > 0) {
        		obj.getMeta().setTag(BaseCoding.toCodingList(ent.tag));
        	}
        	if (ent.security != null && ent.security.size() > 0) {
        		obj.getMeta().setSecurity(BaseCoding.toCodingList(ent.security));
        	}
            obj.setExtension(ent.extension);
            obj.setModifierExtension(ent.modifierExtension);
        }
    }
}