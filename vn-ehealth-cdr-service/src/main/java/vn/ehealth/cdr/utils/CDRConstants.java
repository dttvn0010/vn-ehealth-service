package vn.ehealth.cdr.utils;

public class CDRConstants {

    public static interface TRANGTHAI_DULIEU {
        final public static int DEFAULT = 0;
        final public static int DA_XOA = -1;
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
    
    public static interface LoaiDichVuKT {
        final public static String PHAU_THUAT_THU_THUAT = "SUR";
        final public static String XET_NGHIEM = "LAB";
        final public static String GIAI_PHAU_BENH = "SP";
        final public static String CHAN_DOAN_HINH_ANH = "CA";        
        final public static String THAM_DO_CHUC_NANG = "XRC";
    }   
}
