package vn.ehealth.auth.service;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
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
	@Autowired RoleService roleService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userService.getByUsername(username).orElse(null);
        if(user != null) {
            var grantedAuthorities = new HashSet<GrantedAuthority>();
                        
            if(!StringUtils.isBlank(user.roleId)) {
                var role =  roleService.getById(new ObjectId(user.getId()));
                role.ifPresent(x -> {
                    grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + x.ma));
                });                            
            }

            return new org.springframework.security.core.userdetails.User(user.username, user.password, grantedAuthorities);            
        }        
        
        throw new UsernameNotFoundException(username);
    }
}
