package vn.ehealth.hl7.fhir.core.util;

import java.io.File;

public class FileUtil {
    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
