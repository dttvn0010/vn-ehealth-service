package vn.ehealth.hl7.fhir.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionUtil {

    static Logger logger = LoggerFactory.getLogger(PermissionUtil.class);
    
    public static void checkPermission(String permission) {
        /*
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var authorities = auth.getAuthorities().stream().map(x -> x.getAuthority()).collect(Collectors.toSet());
        
        if(!authorities.contains(permission)) {
            logger.warn("Authentication failed due to insufficient access rights: ");
            throw new AuthenticationException("Authentication failed due to insufficient access rights:" + permission);
        }*/
    }
}