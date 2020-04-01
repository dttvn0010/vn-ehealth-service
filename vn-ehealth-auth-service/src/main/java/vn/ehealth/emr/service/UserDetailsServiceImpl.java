package vn.ehealth.emr.service;

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
	
	@Autowired UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userService.getByUsername(username).orElse(null);
        if(user != null) {
            var grantedAuthorities = new HashSet<GrantedAuthority>();
            var roles = user.getRoles();
            for(var role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.ma));            
            }

            return new org.springframework.security.core.userdetails.User(user.username, user.password, grantedAuthorities);            
        }        
        
        throw new UsernameNotFoundException(username);
    }
}
