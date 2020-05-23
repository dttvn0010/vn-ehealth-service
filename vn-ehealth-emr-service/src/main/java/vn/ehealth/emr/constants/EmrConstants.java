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
	}
	
}
