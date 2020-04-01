package vn.ehealth.auth.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class EmrServiceFactory implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;
  
    
    private static UserService userService;
    private static RoleService roleService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        EmrServiceFactory.applicationContext = applicationContext;        
    }
    
    public static UserService getUserService() {
        if(userService == null) {
            userService = applicationContext.getBean(UserService.class);
        }
        return userService;
    }
    
    public static RoleService getRoleService() {
        if(roleService == null) {
            roleService = applicationContext.getBean(RoleService.class);
        }
        return roleService;
    }     
}
