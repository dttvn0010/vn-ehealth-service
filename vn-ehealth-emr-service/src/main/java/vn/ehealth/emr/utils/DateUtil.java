package vn.ehealth.emr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
			
	final public static String FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
	final public static String FORMAT_DD_MM_YYYY_HH_MM = "dd/MM/yyyy HH:mm";
	final public static String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";	
	final public static String yyyyMMddHHmmss = "yyyyMMddHHmmss";
	
	
	public static String parseDateToString(Date date, String format) {
	    if(date == null) {
	        return null;
	    }
	    
		SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static Date parseStringToDate(String dateStr, String format) {
		SimpleDateFormat sdf = EmrUtils.createSimpleDateFormat(format);
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			logger.error("Wrong format date", e);
			return null;
		}
	}
}
