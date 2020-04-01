package vn.ehealth.cdr.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import vn.ehealth.auth.service.RoleService;
import vn.ehealth.auth.service.UserService;

@Component
public class ServiceFactory implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;
    private static HoSoBenhAnService hoSoBenhAnService;
    private static BenhNhanService benhNhanService;
    private static CoSoKhamBenhService coSoKhamBenhService;    
    
    private static UserService userService;
    private static RoleService roleService;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ServiceFactory.applicationContext = applicationContext;        
    }
    
    public static HoSoBenhAnService getEmrHoSoBenhAnService() {
        if(hoSoBenhAnService == null) {
            hoSoBenhAnService = applicationContext.getBean(HoSoBenhAnService.class);
        }
        return hoSoBenhAnService;
    }
    
    public static BenhNhanService getEmrBenhNhanService() {
        if(benhNhanService == null) {
            benhNhanService = applicationContext.getBean(BenhNhanService.class);
        }
        return benhNhanService;
    }
    
    public static CoSoKhamBenhService getEmrCoSoKhamBenhService() {
        if(coSoKhamBenhService == null) {
            coSoKhamBenhService = applicationContext.getBean(CoSoKhamBenhService.class); 
        }
        return coSoKhamBenhService;        
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
