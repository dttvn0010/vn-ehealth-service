package vn.ehealth.emr.utils;

public class Constants {
    
    public static interface MA_NHOM_DANH_MUC {
        final public static String LOAI_BENH_AN = "LOAI_BENH_AN";
    }
    
    public static interface MA_DANH_MUC {
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
        final public static int NHAP = 0;
        final public static int CHUA_XULY = 1;
        final public static int DA_LUU = 2;
        final public static int DA_XOA = 3;
    }
    
    public static interface NGUON_DU_LIEU {
        final public static int TU_HIS = 1;
    }
    
    //public static final String EMR_MA_LOAI_DOI_TUONG_BHYT = "01";
    
    
    public static interface MA_BENH_AN {
        public static final String NOI_KHOA = "01";
        
        public static final String NHI_KHOA = "02";
        
        public static final String TRUYEN_NHIEM = "03";
        
        public static final String PHU_KHOA = "04";
        
        public static final String SAN_KHOA = "05";
        
        public static final String SO_SINH = "06";
        
        public static final String TAM_THAN = "07";
        
        public static final String DA_LIEU = "08";
        
        public static final String DIEU_DUONG_PHUC_HOI_CHUC_NANG = "09";
        
        public static final String HUYET_HOC_TRUYEN_MAU = "10";
        
        public static final String NGOAI_KHOA = "11";
        
        public static final String BONG = "12";
        
        public static final String UNG_BUOU = "13";
        
        public static final String RANG_HAM_MAT = "14";
            
        public static final String TAI_MUI_HONG = "15";
        
        public static final String MAT = "16";
        
        public static final String LAC_VAN_NHAN = "17";
        
        public static final String MAT_TRE_EM = "18";
        
        public static final String MAT_GLOCOM = "19";
        
        public static final String LAO = "20";
        
        public static final String TAY_CHAN_MIENG = "21";
        
        public static final String TIM_MACH = "22";
        
        public static final String NGOAI_TRU = "23";
        
        public static final String NGOAI_TRU_RANG_HAM_MAT = "24";
        
        public static final String NGOAI_TRU_TAI_MUI_HONG = "25";
        
        public static final String NGOAI_TRU_MAT = "26";
        
        public static final String YHCT_NOI_TRU = "27";
        
        public static final String YHCT_NGOAI_TRU = "28";
        
        public static final String YHCT_XA_PHUONG = "29";
        
        public static final String SAN_PSTW = "30";
        
        public static final String PHU_PSTW = "31";
            
        public static final String SO_SINH_PSTW = "32";
        
        public static final String YHCT_NOI_TRU_BAN_NGAY = "33";
        
        public static final String YHCT_NHA_BA = "34";

        public static final String CAP_CUU = "35";
        
    }    
    
    public static class LoaiThoiDiemTuVong {
        public static final int KHONG_XAC_DINH = -1;
        public static final int KHONG = 0;
        public static final int _24H = 1;
        public static final int _48H = 2;
        public static final int _72H = 3;
        public static final int OTHER = 10;
    }
            
    public static class VaiTroPTTT {
        public static final String PHAU_THUAT_VIEN_CHINH       = "01";
        public static final String PHAU_THUAT_VIEN_PHU         = "02";
        public static final String THU_THUAT_VIEN_CHINH        = "03";
        public static final String THU_THUAT_VIEN_PHU          = "04";
        public static final String GAY_ME_CHINH                = "05";
        public static final String GAY_ME_PHU                  = "06";
        public static final String THANH_VIEN                  = "07";
    }
    
    public static class VaiTroHoiChan {
        public static final String BAC_SY_CHU_TOA      = "01";
        public static final String THU_KY              = "02";
    }
}
