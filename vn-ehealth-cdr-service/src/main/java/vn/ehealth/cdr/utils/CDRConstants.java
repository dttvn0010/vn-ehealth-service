package vn.ehealth.cdr.utils;

public class CDRConstants {

    public static interface TRANGTHAI_DULIEU {
        final public static int DEFAULT = 0;
        final public static int DA_XOA = -1;
    }   
    
    public static interface MA_HANH_DONG {
        final public static String THEM_SUA = "01";
        final public static String XOA = "02";
        final public static String XEM = "03";
        final public static String LUU_TRU = "04";
        final public static String MO_LUU_TRU = "05";
    } 
    
    public static interface TRANGTHAI_HOSO {
        final public static int DA_XOA = -1;
        final public static int CHUA_XULY = 0;
        final public static int DA_LUU = 1;        
    }
    
    public static interface TRANGTHAI_DVKT {
        final public static int DA_XOA = -1;
        final public static int DANG_THUC_HIEN = 0;
        final public static int DA_XONG = 1;        
    }
    
    public static interface TRANGTHAI_DONTHUOC {
        final public static int DA_XOA = -1;
        final public static int DANG_THUC_HIEN = 0;
        final public static int DA_XONG = 1;        
    }
    
    public static interface NGUON_DU_LIEU {
        final public static int TU_HIS = 1;
    }
    
    public static interface LoaiYlenh {
        final public static String YLENH_DIEU_TRI = "01";
        final public static String YLENH_THUOC = "02";
        final public static String YLENH_DVKT = "03";
    }
    
    public static interface LoaiDichVuKT {
        final public static String PHAU_THUAT_THU_THUAT = "SUR";
        final public static String XET_NGHIEM = "LAB";
        final public static String GIAI_PHAU_BENH = "SP";
        final public static String CHAN_DOAN_HINH_ANH = "CA";        
        final public static String THAM_DO_CHUC_NANG = "XRC";
    }   
    
    public static interface ThoiDiemDungThuoc {
        final public static String SANG = "01";
        final public static String TRUA = "02";
        final public static String CHIEU = "03";
        final public static String TOI = "04";
    }

}
