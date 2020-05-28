package vn.ehealth.emr.constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class EmrConstants {

	private static Logger logger = LoggerFactory.getLogger(EmrConstants.class);
	
	public static Properties observationCodes = new Properties();
	
	static {
        try {
            var input = new ClassPathResource("observation_codes.properties").getInputStream();
            observationCodes.load(new InputStreamReader(input, Charset.forName("UTF-8")));
        } catch (IOException e) {
            logger.error("Cannot read observation_codes.properties", e);
        }        
	}
	
	public static interface ObservationCodes {
		public static interface ThongTinVaoVien {
			final public static String LY_DO_VAO_VIEN = observationCodes.getProperty("lyDoVaoVien");
			final public static String VAO_LAN_THU = observationCodes.getProperty("vaoLanThu");
			final public static String VAO_NGAY_THU = observationCodes.getProperty("vaoNgayThu");
		}
		
		public static interface QuaTrinhBenhLy {
			final public static String QUA_TRINH_KHOI_PHAT = observationCodes.getProperty("quaTrinhKhoiPhat");
			final public static String MUC_DO_VA_DIEN_BIEN = observationCodes.getProperty("mucDoVaDienBien");
			final public static String VI_TRI = observationCodes.getProperty("viTri");
			final public static String TONG_QUAN = observationCodes.getProperty("tongQuan");
		}
		
		public static interface ChucNangSong {
		    final public static String HEART_RATE = observationCodes.getProperty("heartRate");
		    final public static String BP_SYSTOLIC = observationCodes.getProperty("bpSystolic");
		    final public static String BP_DIASTOLIC = observationCodes.getProperty("bpDiastolic");
		    final public static String RESPIRATION_RATE = observationCodes.getProperty("respirationRate");
		    final public static String BODY_TEMPERATURE = observationCodes.getProperty("bodyTemperature");
		}
	}
	
}
