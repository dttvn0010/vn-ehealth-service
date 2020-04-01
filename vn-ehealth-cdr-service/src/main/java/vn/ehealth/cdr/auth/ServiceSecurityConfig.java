package vn.ehealth.cdr.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.ehealth.auth.jwt.JWTAuthenticationEntryPoint;
import vn.ehealth.auth.jwt.JWTAuthenticationFilter;


@EnableWebSecurity
public class ServiceSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { 
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }    
    
    @Configuration
    @Order(1)
    public static class BasicAuthSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .antMatcher("/api/basic/**")
                .csrf()
                     .disable()
                .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                .httpBasic();
        }
    }
    
    @Configuration
    @Order(2)
    @EnableGlobalMethodSecurity(
              prePostEnabled = true, 
              securedEnabled = true, 
              jsr250Enabled = true)
    public class JWTAuthSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Autowired
        private JWTAuthenticationEntryPoint unauthorizedHandler;
        
        @Bean
        public JWTAuthenticationFilter jwtAuthenticationFilter() {
            return new JWTAuthenticationFilter();
        }
        
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**")
                .headers()
                    .frameOptions()
                    .disable()
                    .and()
                .csrf()
                    .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(unauthorizedHandler)
                    .and()
                 .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                 .authorizeRequests()
                 	 .antMatchers("/api/**").permitAll()
                     .antMatchers("/api/auth/**").permitAll()
                     .antMatchers(HttpMethod.GET, "/api/user/**").permitAll()
                     .anyRequest().authenticated();
            
            http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }
        
        @Bean
        public AuthenticationManager customAuthenticationManager() throws Exception {
            return authenticationManager();
        }
    }    
}
