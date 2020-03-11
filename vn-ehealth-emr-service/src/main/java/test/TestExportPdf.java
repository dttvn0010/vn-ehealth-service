package test;


import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.utils.EmrUtils;
import vn.ehealth.emr.utils.PDFExportUtil;

public class TestExportPdf {
    
    static ObjectMapper mapper = EmrUtils.createObjectMapper();
    
    static EmrHoSoBenhAn getHsba() throws IOException {
        var file = new ClassPathResource("static/json/hsba.json").getInputStream();
        var jsonSt = new String(file.readAllBytes());
        file.close();
        
        return mapper.readValue(jsonSt, EmrHoSoBenhAn.class);
    }

    public static void main(String[] args) throws Exception {
        
        var hsba = getHsba();
        
        var bytes = PDFExportUtil.exportPdf(hsba, "http://localhost:8080");
        var f = new FileOutputStream("C:/tmp/output.pdf");
        f.write(bytes);
        f.close();
        
        System.out.println("Done");
    }
}
