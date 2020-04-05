package vn.ehealth.cda;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import vn.ehealth.hl7.fhir.core.util.DateUtil;

public class TemplateUtils {

    public static boolean isBlank(String st) {
        return StringUtils.isBlank(st);
    }
    
    public static String randomUUID() {
        return  UUID.randomUUID().toString();
    }
    
    public static String formatDate(String strDate) {
        var d = DateUtil.parseStringToDate(strDate, "yyyy-MM-dd HH:mm:ss");
        return DateUtil.parseDateToString(d, "yyyyMMddHHmmss");
    }
    
    public static String orStr(String st1, String st2) {
        if(!isBlank(st1)) {
            return st1;
        }
        return st2;
    }
    
    public static List<?> subList(List<?> lst, int start) {
        return lst.subList(start, lst.size());
    }
    
    public static String castToNull(String st) {
        if(StringUtils.isBlank(st)) {
            return null;
        }
        return st;
    }
    
    public static <T> T atList(List<T> lst, int index) {
        if(lst != null) {
            if(index < 0) {
                index = lst.size() - index;
            }
            
            if(index >= 0 && index < lst.size()) {
                return lst.get(index);
            }
        }
        return null;
    }
    
    public static String createText(String text) {
        if(!StringUtils.isBlank(text)) {
            return "<text>" + text + "</text>";
        }
        return "";
    }
}
