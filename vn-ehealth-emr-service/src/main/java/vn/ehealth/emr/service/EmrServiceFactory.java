package vn.ehealth.emr.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class EmrServiceFactory implements ApplicationContextAware  {

    private static ApplicationContext applicationContext;
    private static EmrHoSoBenhAnService emrHoSoBenhAnService;
    private static EmrBenhNhanService emrBenhNhanService;
    private static EmrCoSoKhamBenhService emrCoSoKhamBenhService;
    private static EmrVaoKhoaService emrVaoKhoaService;
    
    private static EmrChamSocService emrChamSocService;
    private static EmrQuaTrinhChamSocService emrQuaTrinhChamSocService;
    
    //private static EmrDieuTriService emrDieuTriService;
    private static EmrQuaTrinhDieuTriService emrQuaTrinhDieuTriService;
    
    //private static EmrChucNangSongService emrChucNangSongService;
    private static EmrChucNangSongChiTietService emrChucNangSongChiTietService;
    
    //private static EmrHoiChanService emrHoiChanService;
    private static EmrThanhVienHoiChanService emrThanhVienHoiChanService;
    
    private static EmrXetNghiemService emrXetNghiemService;
    private static EmrXetNghiemDichVuService emrXetNghiemDichVuService;
    private static EmrXetNghiemKetQuaService emrXetNghiemKetQuaService;
    private static EmrDonThuocChiTietService emrDonThuocChiTietService;
    
    private static UserService userService;
    private static RoleService roleService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        EmrServiceFactory.applicationContext = applicationContext;        
    }
    
    public static EmrHoSoBenhAnService getEmrHoSoBenhAnService() {
        if(emrHoSoBenhAnService == null) {
            emrHoSoBenhAnService = applicationContext.getBean(EmrHoSoBenhAnService.class);
        }
        return emrHoSoBenhAnService;
    }
    
    public static EmrBenhNhanService getEmrBenhNhanService() {
        if(emrBenhNhanService == null) {
            emrBenhNhanService = applicationContext.getBean(EmrBenhNhanService.class);
        }
        return emrBenhNhanService;
    }
    
    public static EmrCoSoKhamBenhService getEmrCoSoKhamBenhService() {
        if(emrCoSoKhamBenhService == null) {
            emrCoSoKhamBenhService = applicationContext.getBean(EmrCoSoKhamBenhService.class); 
        }
        return emrCoSoKhamBenhService;        
    }
    
    public static EmrVaoKhoaService getEmrVaoKhoaService() {
        if(emrVaoKhoaService == null) {
            emrVaoKhoaService = applicationContext.getBean(EmrVaoKhoaService.class);            
        }
        return emrVaoKhoaService;
    }
    
    public static EmrChamSocService getEmrChamSocService() {
        if(emrChamSocService == null) {
            emrChamSocService = applicationContext.getBean(EmrChamSocService.class);
        }
        
        return emrChamSocService;
    }
    
    public static EmrQuaTrinhChamSocService getEmrQuaTrinhChamSocService() {
        if(emrQuaTrinhChamSocService == null) {
            emrQuaTrinhChamSocService = applicationContext.getBean(EmrQuaTrinhChamSocService.class);
        }
        
        return emrQuaTrinhChamSocService;
    }
    
    
    public static EmrQuaTrinhDieuTriService getEmrQuaTrinhDieuTriService() {
        if(emrQuaTrinhDieuTriService == null) {
            emrQuaTrinhDieuTriService = applicationContext.getBean(EmrQuaTrinhDieuTriService.class);
        }
        
        return emrQuaTrinhDieuTriService;
    }
    
    public static EmrChucNangSongChiTietService getEmrChucNangSongChiTietService() {
        if(emrChucNangSongChiTietService == null) {
            emrChucNangSongChiTietService = applicationContext.getBean(EmrChucNangSongChiTietService.class);
        }
        
        return emrChucNangSongChiTietService;
    }
    
    public static EmrThanhVienHoiChanService getEmrThanhVienHoiChanService() {
        if(emrThanhVienHoiChanService == null) {
            emrThanhVienHoiChanService = applicationContext.getBean(EmrThanhVienHoiChanService.class);
        }
        
        return emrThanhVienHoiChanService;
    }
    
    public static EmrXetNghiemService getEmrXetNghiemService() {
        if(emrXetNghiemService == null) {
            emrXetNghiemService = applicationContext.getBean(EmrXetNghiemService.class);
        }
        
        return emrXetNghiemService;
    }
    
    public static EmrXetNghiemDichVuService getEmrXetNghiemDichVuService() {
        if(emrXetNghiemDichVuService == null) {
            emrXetNghiemDichVuService = applicationContext.getBean(EmrXetNghiemDichVuService.class);
        }
        
        return emrXetNghiemDichVuService;
    }
    
    public static EmrXetNghiemKetQuaService getEmrXetNghiemKetQuaService() {
        if(emrXetNghiemKetQuaService == null) {
            emrXetNghiemKetQuaService = applicationContext.getBean(EmrXetNghiemKetQuaService.class);
        }
        
        return emrXetNghiemKetQuaService;
    }
    
    public static EmrDonThuocChiTietService getEmrDonThuocChiTietService() {
        if(emrDonThuocChiTietService == null) {
            emrDonThuocChiTietService = applicationContext.getBean(EmrDonThuocChiTietService.class);            
        }
        return emrDonThuocChiTietService;        
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
