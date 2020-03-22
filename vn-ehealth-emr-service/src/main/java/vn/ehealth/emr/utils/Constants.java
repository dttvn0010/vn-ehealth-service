package vn.ehealth.emr.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Constants {
    
    private static Logger logger = LoggerFactory.getLogger(Constants.class);
            
    public static Properties codeSystems = new Properties();
    public static Properties extensionURLs = new Properties();
    
    static {
        try {
            var input = new ClassPathResource("code_systems.properties").getInputStream();
            codeSystems.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read code_systems.properties", e);
        }
        
        try {
            var input = new ClassPathResource("extension_urls.properties").getInputStream();
            extensionURLs.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read extension_urls.properties", e);
        }
    }
    
    public static interface TRANGTHAI_DULIEU {
        final public static int DEFAULT = 0;
        final public static int DA_XOA = -1;
    }
    
    public static interface MA_NHOM_DANH_MUC {
        final public static String LOAI_BENH_AN = "LOAI_BENH_AN";
    }
    
    public static interface MA_DANH_MUC {
    }
    
    
    public static interface MA_VAI_TRO {
        final public static String BENH_NHAN = "01";
        final public static String BAC_SI = "02";
        final public static String ADMIN = "03";
        final public static String SUPER_USER = "04";
    }
    
    public static interface MA_HANH_DONG {
        final public static String TAO_MOI = "01";
        final public static String CHINH_SUA = "02";
        final public static String XOA = "03";
        final public static String XEM = "04";
        final public static String LUU_TRU = "05";
        final public static String MO_LUU_TRU = "06";
    } 
    
    public static interface TRANGTHAI_HOSO {
        final public static int DA_XOA = -1;
        final public static int CHUA_XULY = 0;
        final public static int DA_LUU = 1;        
    }
    
    public static interface NGUON_DU_LIEU {
        final public static int TU_HIS = 1;
    }
    
    public static String getProperty(String property) {
        return codeSystems != null? codeSystems.getProperty(property, "") : "";
    }
    
    public static interface CodeSystemValue {
        final public static String PHONE = "phone";
        final public static String EMAIL = "email";
        
        final public static String CMND = codeSystems.getProperty("cmnd");
        final public static String THE_BHYT = codeSystems.getProperty("the_bhyt");
        
        final public static String DVHC = codeSystems.getProperty("dvhc");
        final public static String QUOC_GIA = codeSystems.getProperty("quoc_gia");
        
        final public static String DAN_TOC = codeSystems.getProperty("dantoc");
        final public static String TON_GIAO = codeSystems.getProperty("tongiao");
        final public static String NGHE_NGHIEP = codeSystems.getProperty("nghe_nghiep");
        final public static String DOI_TUONG_TAI_CHINH = codeSystems.getProperty("doituong_taichinh");
        
        final public static String CHUNG_CHI_HANH_NGHE = codeSystems.getProperty("chung_chi_hanh_nghe");
        
        final public static String CO_SO_KHAM_BENH = codeSystems.getProperty("co_so_kham_benh");
        final public static String DOT_KHAM_BENH = codeSystems.getProperty("dot_kham_benh");
        final public static String LOAI_KHAM_BENH = codeSystems.getProperty("loai_kham_benh");
        final public static String KHOA_DIEU_TRI = codeSystems.getProperty("khoa_dieu_tri");
        
        
        final public static String DICH_VU_KY_THUAT = codeSystems.getProperty("dich_vu_ky_thuat");
        final public static String LOAI_DICH_VU_KY_THUAT = codeSystems.getProperty("loai_dich_vu_ky_thuat");

        final public static String VAI_TRO_PTT = codeSystems.getProperty("vaitro_pttt");
        final public static String VI_TRI_MAU_SINH_THIET = codeSystems.getProperty("vi_tri_mau_sinh_thiet");
    }
    
    
    public static interface ExtensionURL {
        final public static String DVHC = extensionURLs.getProperty("dvhc");
        final public static String DAN_TOC = extensionURLs.getProperty("dantoc");
        final public static String TON_GIAO = extensionURLs.getProperty("tongiao");
        final public static String NGHE_NGHIEP = extensionURLs.getProperty("nghe_nghiep");
        final public static String QUOC_TICH = extensionURLs.getProperty("quoctich");
        
        final public static String TRINH_TU_PTTT = extensionURLs.getProperty("pttt.trinhtu");
        final public static String DAI_THE_GPB = extensionURLs.getProperty("gpb.daithe");
        final public static String VI_THE_GPB = extensionURLs.getProperty("gpb.vithe");
    }
    
    public static interface LoaiDichVuKT {
        final public static String PHAU_THUAT_THU_THUAT = "SUR";
        final public static String XET_NGHIEM = "LAB";
        final public static String GIAI_PHAU_BENH = "SP";
        final public static String CHAN_DOAN_HINH_ANH = "CA";        
        final public static String THAM_DO_CHUC_NANG = "XRC";
    }
    
}
