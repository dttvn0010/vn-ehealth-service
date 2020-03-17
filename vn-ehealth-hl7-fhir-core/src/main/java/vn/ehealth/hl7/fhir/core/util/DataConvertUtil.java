package vn.ehealth.hl7.fhir.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Meta;

import vn.ehealth.hl7.fhir.core.entity.BaseCoding;
import vn.ehealth.hl7.fhir.core.entity.BaseResource;

public class DataConvertUtil {
    
    public static class ObjectWrapper<T> {
        private T obj;
        
        public ObjectWrapper(T obj) {
            this.obj = obj;
        }
        
        public <U> U evalIf(boolean cond, Function<T, U> f) {
            if(cond && obj != null) {
                return f.apply(obj);
            }
            return null;
        }
        
        public <U,V> V evalIf(boolean cond, Function<T, U> f1, Function<U, V> f2) {
            if(cond && obj != null) {
                var u = f1.apply(obj);
                if(u != null) return f2.apply(u);
            }
            return null;
        }
    }
    
    @SafeVarargs
    public static<T> List<T> listOf(T...arr) {
        var lst = new ArrayList<T>();
        for(T obj: arr) {
            if(obj != null) lst.add(obj);
        }
        return lst;
    }
    
    public static <T, U> List<U> transform(List<T> lst, Function<T, U> func) {
        if(lst != null) {
            return lst.stream().map(x -> func.apply(x))
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