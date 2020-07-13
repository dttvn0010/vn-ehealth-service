package vn.ehealth.hl7.fhir.core.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Constants {
    
    private static Logger logger = LoggerFactory.getLogger(Constants.class);
    
    public static Properties identifierSystems = new Properties();
    public static Properties codeSystems = new Properties();
    public static Properties extensionURLs = new Properties();
    
    static {
        try {
            var input = new ClassPathResource("identifier_systems.properties").getInputStream();
            identifierSystems.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read identifier_systems.properties", e);
        }
        
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
    

    public static interface IdentifierSystem{
        final public static String NATIONAL_ID = identifierSystems.getProperty("national_id");
        final public static String INSURANCE_NUMBER = identifierSystems.getProperty("insurancenumber");
        final public static String PROVIDER = identifierSystems.getProperty("provider");
        final public static String PRACTITIONER = identifierSystems.getProperty("practitioner");        
        final public static String MEDICAL_RECORD = identifierSystems.getProperty("medical-record");
        final public static String MEDICATION_REQUEST = identifierSystems.getProperty("medication-request");
    }
    
    public static interface CodeSystemValue {
        // hl7
        final public static String ICD10 = codeSystems.getProperty("icd-10");
        final public static String GENDER = codeSystems.getProperty("gender");
        final public static String DIAGNOSIS_ROLE = codeSystems.getProperty("diagnosis-role");
        final public static String DIAGNOSTIC_SERVICE_SECTIONS = codeSystems.getProperty("diagnostic-service-sections");
        final public static String ALLERGY_INTOLERANCE_CLINICAL = codeSystems.getProperty("allergyintolerance-clinical");        
        final public static String SUBSTANCE_CODE = codeSystems.getProperty("substance-code");
        final public static String ROUTE_CODE = codeSystems.getProperty("route-codes");        
        final public static String CLINICAL_FINDING = codeSystems.getProperty("clinical-findings");        
        final public static String ALLERGY_INTOLERANCE_CRITICALITY = codeSystems.getProperty("allergy-intolerance-criticality");
        final public static String ALLERGY_INTOLERANCE_CODE = codeSystems.getProperty("allergyintolerance-code");
        final public static String VACCINE_CODE = codeSystems.getProperty("vaccine-code");
        
        //emr.com.vn
        
        final public static String DVHC = codeSystems.getProperty("DiaDdanhHanhChinh");
        final public static String QUOC_GIA = codeSystems.getProperty("QuocGia");        
        final public static String DAN_TOC = codeSystems.getProperty("DanToc");
        final public static String TON_GIAO = codeSystems.getProperty("TonGiao");
        final public static String NGHE_NGHIEP = codeSystems.getProperty("NgheNghiep");        
        final public static String DICH_VU_KY_THUAT = codeSystems.getProperty("DVKyThuat");
        final public static String VAI_TRO_PTTT = codeSystems.getProperty("VaiTroPTTT");
        final public static String CHI_SO_XET_NGHIEM = codeSystems.getProperty("ChiSoXetNghiem");
        final public static String DICH_KET_QUA_XET_NGHIEM = codeSystems.getProperty("DichKetQuaXetNghiem");
        final public static String VI_TRI_MAU_SINH_THIET = codeSystems.getProperty("ViTriMauSinhThiet");        
        final public static String DM_THUOC = codeSystems.getProperty("Thuoc");
        final public static String DM_DUONG_DUNG_THUOC = codeSystems.getProperty("DuongDungThuoc");
        final public static String DM_THOI_DIEM_DUNG_THUOC = codeSystems.getProperty("ThoiDiemDungThuoc");
        final public static String DOI_TUONG_BH = codeSystems.getProperty("doituongbaohiem");
        final public static String NOIKCBBD = codeSystems.getProperty("noikcbbd");
        final public static String MUC_HUONG_BHYT = codeSystems.getProperty("muchuongbhyt");
        final public static String BH_LIENTIEP_5NAM = codeSystems.getProperty("bhlientiep5nam");
        //bo_sung
        final public static String ICD9 = codeSystems.getProperty("icd-9");
        final public static String ICD9_KCB = codeSystems.getProperty("icd-9-kcb");
        final public static String ICD10_KCB = codeSystems.getProperty("icd-10-kcb");
        final public static String DM_TUONG_DUONG_ICD9 = codeSystems.getProperty("DanhMucTuongDuongICD9");
        final public static String DM_TRIEU_CHUNG_THEO_ICD10 = codeSystems.getProperty("DanhMucTrieuChungTheoICD10");
        final public static String DM_PHAT_HIEN_LAM_SANG = codeSystems.getProperty("DanhMucPhatHienLamSang");
        final public static String DM_TAC_NHAN_GAY_DI_UNG = codeSystems.getProperty("DanhMucTacNhanGayDiUng");
        final public static String DM_VACCINE_CDC = codeSystems.getProperty("DanhMucVaccine_CDC");
        final public static String DM_GIAI_PHAU = codeSystems.getProperty("DanhMucGiaiPhau");
        final public static String DM_CO_SO_Y_TE = codeSystems.getProperty("DanhMucCoSoYte");
        final public static String DM_ICF = codeSystems.getProperty("DanhMucICF");
        
    }
    
    
    public static interface ExtensionURL {
        final public static String DVHC = extensionURLs.getProperty("dvhc");
        final public static String DAN_TOC = extensionURLs.getProperty("dantoc");
        final public static String TON_GIAO = extensionURLs.getProperty("tongiao");
        final public static String NGHE_NGHIEP = extensionURLs.getProperty("nghe_nghiep");
        final public static String QUOC_TICH = extensionURLs.getProperty("quoctich");
        final public static String DOI_TUONG_BH = extensionURLs.getProperty("doituongbaohiem");
        final public static String NOIKCBBD = extensionURLs.getProperty("noikcbbd");
        final public static String MUC_HUONG_BHYT = extensionURLs.getProperty("muchuongbhyt");
        final public static String BH_LIENTIEP_5NAM = extensionURLs.getProperty("bhlientiep5nam");
       
        
        final public static String TRINH_TU_PTTT = extensionURLs.getProperty("pttt.trinhtu");
        final public static String DAI_THE_GPB = extensionURLs.getProperty("gpb.daithe");
        final public static String VI_THE_GPB = extensionURLs.getProperty("gpb.vithe");
    }    
     
}
