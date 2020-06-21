package vn.ehealth.cdr.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ServiceFactory implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;
    private static HoSoBenhAnService hoSoBenhAnService;
    private static BenhNhanService benhNhanService;
    private static CanboYteService canboYteService;
    private static CoSoKhamBenhService coSoKhamBenhService;   
    private static DonThuocChiTietService donThuocChiTietService;
    private static UongThuocService uongThuocService;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        ServiceFactory.applicationContext = applicationContext;        
    }
    
    public static HoSoBenhAnService getHoSoBenhAnService() {
        if(hoSoBenhAnService == null) {
            hoSoBenhAnService = applicationContext.getBean(HoSoBenhAnService.class);
        }
        return hoSoBenhAnService;
    }
    
    public static BenhNhanService getBenhNhanService() {
        if(benhNhanService == null) {
            benhNhanService = applicationContext.getBean(BenhNhanService.class);
        }
        return benhNhanService;
    }
    
    public static CanboYteService getCanboYteService() {
        if(canboYteService == null) {
            canboYteService = applicationContext.getBean(CanboYteService.class);
        }
        return canboYteService;
    }
    
    public static CoSoKhamBenhService getCoSoKhamBenhService() {
        if(coSoKhamBenhService == null) {
            coSoKhamBenhService = applicationContext.getBean(CoSoKhamBenhService.class); 
        }
        return coSoKhamBenhService;        
    }
    
    public static DonThuocChiTietService getDonThuocChiTietService() {
        if(donThuocChiTietService == null) {
            donThuocChiTietService = applicationContext.getBean(DonThuocChiTietService.class); 
        }
        return donThuocChiTietService;        
    }
    
    public static UongThuocService getUongThuocService() {
        if(uongThuocService == null) {
            uongThuocService = applicationContext.getBean(UongThuocService.class); 
        }
        return uongThuocService;        
    }
}
