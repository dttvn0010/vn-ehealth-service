package vn.ehealth.emr.utils;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import vn.ehealth.emr.model.User;
import vn.ehealth.emr.service.EmrServiceFactory;

public class UserUtil {

    public static Optional<User> getCurrentUser() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if(userDetails instanceof UserDetails) {
            String username = ((UserDetails) userDetails).getUsername();
            return EmrServiceFactory.getUserService().getByUsername(username);
        }
        
        return Optional.ofNullable(null);
        
    }
}
