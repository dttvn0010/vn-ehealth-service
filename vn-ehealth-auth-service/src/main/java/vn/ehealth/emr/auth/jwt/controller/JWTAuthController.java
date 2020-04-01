package vn.ehealth.emr.auth.jwt.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ehealth.emr.auth.jwt.JWTTokenProvider;

@RestController
@RequestMapping("/api/auth")
public class JWTAuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    JWTTokenProvider tokenProvider;
    
    @Autowired
    UserDetailsService userDetailsService;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(Map.of("accessToken", jwt, "tokenType", "Bearer"));
    }
    
    @GetMapping("/get_permissions")
    public ResponseEntity<?> getPermissions(@RequestParam String username) {
        boolean isAdmin = "admin".equals(username);
        
        if(isAdmin) {
            return ResponseEntity.ok(Map.of("all", true));
        }
                
        var permissions = List.of(
                    Map.of(
                            "tab", "tab_hsba",
                            "items", List.of("hsba_chuaxuly", "hsba_daluu")
                    )
                );
        
        return ResponseEntity.ok(Map.of("all", false, "permissions", permissions));
    }
    
    @GetMapping("/check_page_permission")
    public boolean checkPagePermission(@RequestParam String username, @RequestParam String page) {
        if("test".equals(page)) {
            return false;
        }
        return true;
    }
}
