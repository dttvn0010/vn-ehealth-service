
package test;

import java.io.FileInputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.util.JRSaver;

public class CompileReport {

    static String[] paths1 = {
            "src\\main\\resources\\report\\BenhAn\\YHCTNoiTru\\Benh-an-YHCT-Noi-tru.jasper"
    };
    
    static String[] paths = {            
            "src\\main\\resources\\report\\BenhAn\\BenhAn.jasper",
            "src\\main\\resources\\report\\BenhAn\\BAXP\\BenhAn-BAXP.jasper",
            "src\\main\\resources\\report\\BenhAn\\BONG\\BenhAn-BONG.jasper",
            "src\\main\\resources\\report\\BenhAn\\DALIEU\\BenhAn-DALIEU.jasper",
            "src\\main\\resources\\report\\BenhAn\\DauHieuSinhTon\\DauHieuSinhTon.jasper",
            "src\\main\\resources\\report\\BenhAn\\DauHieuSinhTon\\DauHieuSinhTonFull.jasper",
            "src\\main\\resources\\report\\BenhAn\\HHTM\\BenhAn-HHTM.jasper",
            "src\\main\\resources\\report\\BenhAn\\LAO\\BenhAn-LAO.jasper",
            "src\\main\\resources\\report\\BenhAn\\MAT\\Benh-an-Mat-Glocom.jasper",
            "src\\main\\resources\\report\\BenhAn\\MAT\\Benh-An-Mat-Lac.jasper",
            "src\\main\\resources\\report\\BenhAn\\MAT\\BenhAn-Mat.jasper",
            "src\\main\\resources\\report\\BenhAn\\MAT\\BenhAnMatTE.jasper",
            "src\\main\\resources\\report\\BenhAn\\NGOAIKHOA\\BenhAn-NGOAIKHOA.jasper",
            "src\\main\\resources\\report\\BenhAn\\NGOAITRU\\Benh-an-Ngoai-tru.jasper",
            "src\\main\\resources\\report\\BenhAn\\NGOAITRU-MAT\\BenhAn-NgoaiTru_Mat.jasper",
            "src\\main\\resources\\report\\BenhAn\\NhaBaYHCT\\Nha-ba-YHCT.jasper",
            "src\\main\\resources\\report\\BenhAn\\NHIKHOA\\BenhAn-NHIKHOA.jasper",
            "src\\main\\resources\\report\\BenhAn\\NOIKHOA\\BenhAn-NOIKHOA.jasper",
            "src\\main\\resources\\report\\BenhAn\\PHCN\\BenhAn-PHCN.jasper",
            "src\\main\\resources\\report\\BenhAn\\PHUKHOA\\BenhAnPhuKhoa.jasper",
            "src\\main\\resources\\report\\BenhAn\\RHM\\BenhAn-RHM-NgoaiTru.jasper",
            "src\\main\\resources\\report\\BenhAn\\RHM\\BenhAn-RHM.jasper",
            "src\\main\\resources\\report\\BenhAn\\San-Khoa\\BenhAnSanKhoa.jasper",
            "src\\main\\resources\\report\\BenhAn\\San-Khoa\\BenhAnSanKhoa_subreport1.jasper",
            "src\\main\\resources\\report\\BenhAn\\SOSINH\\BenhAn-SOSINH.jasper",
            "src\\main\\resources\\report\\BenhAn\\TAMTHAN\\BenhAn-TAMTHAN.jasper",
            "src\\main\\resources\\report\\BenhAn\\TAYCHANMIENG\\BenhAnTayChanMieng.jasper",
            "src\\main\\resources\\report\\BenhAn\\TIMMACH\\BenhAn-TimMach.jasper",
            "src\\main\\resources\\report\\BenhAn\\TMH\\BenhAn-TMH-NgoaiTru.jasper",
            "src\\main\\resources\\report\\BenhAn\\TMH\\BenhAn-TMH.jasper",
            "src\\main\\resources\\report\\BenhAn\\ToBAFooter\\ToBAFooter.jasper",
            "src\\main\\resources\\report\\BenhAn\\TRUYENNHIEM\\BenhAnTruyenNhiem.jasper",
            "src\\main\\resources\\report\\BenhAn\\UNGBUOU\\BenhAn-UngBuou.jasper",
            "src\\main\\resources\\report\\BenhAn\\YHCTNgoaiTru\\Benh-an-YHCT-Ngoai-tru.jasper",
            "src\\main\\resources\\report\\BenhAn\\YHCTNoiTru\\Benh-an-YHCT-Noi-tru.jasper",
            "src\\main\\resources\\report\\BenhAn\\YHCTNoiTruBanNgay\\Benh-an-YHCT-Noi-Tru-BN.jasper"            
    };
    
    public static void main(String[] args) throws JRException, IOException {
        System.out.println(System.getProperty("user.dir"));
        //String inPath = "src\\main\\resources\\report\\BenhAn\\SOSINH\\BenhAn-SOSINH.jrxml";
        //String inPath = "src\\main\\resources\\report\\BenhAn\\BAXP/BenhAn-BAXP.jrxml";
        //String outPath = inPath.replace(".jrxml", ".jasper");
        for(var outPath : paths) {
            var inPath = outPath.replace(".jasper", ".jrxml");
            
            System.out.println("Compile : " + inPath);
        
            var inputStream = new FileInputStream(inPath);
            var jasperReport = JasperCompileManager.compileReport(inputStream);
            JRSaver.saveObject(jasperReport, outPath);
            inputStream.close();
        }
                
    }
}
