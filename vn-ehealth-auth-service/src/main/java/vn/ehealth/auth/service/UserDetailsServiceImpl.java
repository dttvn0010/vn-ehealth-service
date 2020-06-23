package vn.ehealth.auth.service;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userService.getByUsername(username).orElse(null);
        if(user != null) {
            var grantedAuthorities = new HashSet<GrantedAuthority>();
            var role = user.getRole();  
            if(role != null) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.ma));
                if(role.privileges != null) {
                    for(String privilege : role.privileges) {
                        grantedAuthorities.add(new SimpleGrantedAuthority(privilege));
                    }
                }
            }

            return new org.springframework.security.core.userdetails.User(user.username, user.password, grantedAuthorities);            
        }        
        
        throw new UsernameNotFoundException(username);
    }
}
