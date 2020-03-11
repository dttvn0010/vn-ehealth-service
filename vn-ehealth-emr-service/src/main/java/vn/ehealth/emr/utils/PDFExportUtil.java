package vn.ehealth.emr.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import org.springframework.core.io.ClassPathResource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import vn.ehealth.emr.model.EmrHoSoBenhAn;
import vn.ehealth.emr.utils.Constants.MA_BENH_AN;

public class PDFExportUtil {
    
    static String getRealPath(String path) {
        return "src/main/resources/" + path;
    }
    
    static boolean hasPrivilege(String permission) {
        return true;
    }
       
    static Map<String, String> messages = new HashMap<>();
    
    static {
        try {
            var file = new ClassPathResource("message.properties").getInputStream();
            var reader = new BufferedReader(new InputStreamReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                String[] arr = line.split("=");
                if(arr.length == 2) {
                    messages.put(arr[0].trim(), arr[1].trim());
                }
            }            
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    static String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }
    
    public static byte[] exportPdf(@Nonnull EmrHoSoBenhAn hsba,
                                    @Nonnull String serverBaseURL) throws JRException, IOException {        
        
        String jrxmlFile;
        InputStream jasperIs;
        
        JasperPrint jasperPrint     = null;
        Map<String, Object> params  = new HashMap<>();
       
        if(hsba != null){
            /*EmrLogTruyCapHsba emrLogTruyCapHsba = new EmrLogTruyCapHsba();
            emrLogTruyCapHsba.setEmrHoSoBenhAn(danhSachHSBA);
            emrLogTruyCapHsba.setNgaytao(new Date());
            emrLogTruyCapHsba.setNguoiTruyCap(getAuthenticatedUser());
            emrLogTruyCapHsba.setThoidiemtruycap(new Date());*/
            
            params.put("danhSachHSBA", hsba);
            
            String headerPath;
            headerPath = getRealPath("report/Header");
            params.put("headerPath", headerPath);
            
            
            String duongDanFileDinhKem, duongDanFileDinhKemOnline ;
            duongDanFileDinhKem = serverBaseURL + "/file";
            params.put("duongDanFileDinhKem", duongDanFileDinhKem);
            
            duongDanFileDinhKemOnline = serverBaseURL + "/dtt/hoSoBenhAn/xembaocao/xemFile.htm?id=";
            params.put("duongDanFileDinhKemOnline", duongDanFileDinhKemOnline); 
            
            params.put("duongDanHost", serverBaseURL);
            
            params.put("SUBREPORT_DAUHIEUSINHTON_DIR", getRealPath("report/BenhAn/DauHieuSinhTon")); 
            
            params.put("IMG_DIR", getRealPath("report/images"));
            
            List<EmrHoSoBenhAn> dsHsba = new ArrayList<>();
            dsHsba.add(hsba);
            
            String maLoaiBenhAn = hsba.emrDmLoaiBenhAn != null? hsba.emrDmLoaiBenhAn.ma : "";
            
            params.put("SUBREPORT_DIR", getRealPath("report/BenhAn/ToBAFooter"));
            
            // Check loai ho so benh an
            if (MA_BENH_AN.NOI_KHOA.equals(maLoaiBenhAn)) {
                jrxmlFile = getRealPath("report/BenhAn/NOIKHOA/BenhAn-NOIKHOA.jasper");
                
            } else if (MA_BENH_AN.NHI_KHOA.equals(maLoaiBenhAn)) {
                jrxmlFile = getRealPath("report/BenhAn/NHIKHOA/BenhAn-NHIKHOA.jasper");
                
            } else if (MA_BENH_AN.TRUYEN_NHIEM.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/TRUYENNHIEM/BenhAnTruyenNhiem.jasper");
                
            } else if (MA_BENH_AN.PHU_KHOA.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/PHUKHOA/BenhAnPhuKhoa.jasper");
                
            } else if (MA_BENH_AN.SAN_KHOA.equals(maLoaiBenhAn)) { 
                
                params.put("SUBREPORT_DIR_TIEN_SU", getRealPath("report/BenhAn/San-Khoa"));
                jrxmlFile = getRealPath("report/BenhAn/San-Khoa/BenhAnSanKhoa.jasper");                    
                
            } else if (MA_BENH_AN.SO_SINH.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/SOSINH/BenhAn-SOSINH.jasper");
                
            } else if (MA_BENH_AN.TAM_THAN.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/TAMTHAN/BenhAn-TAMTHAN.jasper");
                
            } else if (MA_BENH_AN.DA_LIEU.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/DALIEU/BenhAn-DALIEU.jasper");
                
            } else if (MA_BENH_AN.DIEU_DUONG_PHUC_HOI_CHUC_NANG.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/PHCN/BenhAn-PHCN.jasper");
                
            } else if (MA_BENH_AN.HUYET_HOC_TRUYEN_MAU.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/HHTM/BenhAn-HHTM.jasper");
                
            } else if (MA_BENH_AN.NGOAI_KHOA.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/NGOAIKHOA/BenhAn-NGOAIKHOA.jasper");
                
            } else if (MA_BENH_AN.BONG.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/BONG/BenhAn-BONG.jasper");
                
            } else if (MA_BENH_AN.UNG_BUOU.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/UNGBUOU/BenhAn-UngBuou.jasper");
                
            } else if (MA_BENH_AN.RANG_HAM_MAT.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/RHM/BenhAn-RHM.jasper");
                
            } else if (MA_BENH_AN.TAI_MUI_HONG.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/TMH/BenhAn-TMH.jasper");
                
            } else if (MA_BENH_AN.MAT.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/MAT/BenhAn-Mat.jasper");
                
            } else if (MA_BENH_AN.LAC_VAN_NHAN.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/MAT/Benh-An-Mat-Lac.jasper");
                
            } else if (MA_BENH_AN.MAT_TRE_EM.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/MAT/BenhAnMatTE.jasper");
                
            } else if (MA_BENH_AN.MAT_GLOCOM.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/MAT/Benh-an-Mat-Glocom.jasper");
                
            } else if (MA_BENH_AN.LAO.equals(maLoaiBenhAn)) {
                jrxmlFile = getRealPath("report/BenhAn/LAO/BenhAn-LAO.jasper");
                
            } else if (MA_BENH_AN.TAY_CHAN_MIENG.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/TAYCHANMIENG/BenhAnTayChanMieng.jasper");
                
            } else if (MA_BENH_AN.TIM_MACH.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/TIMMACH/BenhAn-TimMach.jasper");
                
            } else if (MA_BENH_AN.NGOAI_TRU.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/NGOAITRU/Benh-an-Ngoai-tru.jasper");
                
            }else if (MA_BENH_AN.NGOAI_TRU_TAI_MUI_HONG.equals(maLoaiBenhAn)) {
                jrxmlFile = getRealPath("report/BenhAn/TMH/BenhAn-TMH-NgoaiTru.jasper");
                
            }else if (MA_BENH_AN.NGOAI_TRU_MAT.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/NGOAITRU-MAT/BenhAn-NgoaiTru_Mat.jasper");
                
            }else if (MA_BENH_AN.NGOAI_TRU_RANG_HAM_MAT.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/RHM/BenhAn-RHM-NgoaiTru.jasper");
                
            }else if (MA_BENH_AN.YHCT_NOI_TRU.equals(maLoaiBenhAn)) {
                jrxmlFile = getRealPath("report/BenhAn/YHCTNoiTru/Benh-an-YHCT-Noi-tru.jasper");
                
            }else if (MA_BENH_AN.YHCT_NGOAI_TRU.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/YHCTNgoaiTru/Benh-an-YHCT-Ngoai-tru.jasper");
                
            }else if (MA_BENH_AN.YHCT_NOI_TRU_BAN_NGAY.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/YHCTNoiTruBanNgay/Benh-an-YHCT-Noi-Tru-BN.jasper");
                
            }else if (MA_BENH_AN.YHCT_NHA_BA.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/NhaBaYHCT/Nha-ba-YHCT.jasper");
                
            }else if (MA_BENH_AN.YHCT_XA_PHUONG.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/BAXP/BenhAn-BAXP.jasper");
                
            }/*                 
                 else if (Constants.EMR_MA_LOAI_BENH_AN_SO_SINH_PSTW.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/SOSINH/BenhAnSoSinh_PSTW.jasper");
                
            } else if (Constants.EMR_MA_LOAI_BENH_AN_PHU_PSTW.equals(maLoaiBenhAn)) { 
                jrxmlFile = getRealPath("report/BenhAn/PHUKHOA-PHUSANTW/BenhAnPhuKhoa(PSTW).jasper");
                
            } else if (Constants.EMR_MA_LOAI_BENH_AN_SAN_PSTW.equals(maLoaiBenhAn)) { 
                params.put("SUBREPORT_DIR", getRealPath("report/BenhAn/SANKHOA_PSTW"));  
                var emrCkTienSuSanKhoaChiTiets = danhSachHSBA.emrBenhAn.emrCkTienSuSanKhoa.emrCkTienSuSanKhoaChiTiets;
                
              params.put("TTCTTIENSUSANKHOA", emrCkTienSuSanKhoaChiTiets);
              jrxmlFile = getRealPath("report/BenhAn/SANKHOA_PSTW/BenhAnSanKhoa_PSTW.jasper");
          }*/else {
              
              jrxmlFile = getRealPath("report/BenhAn/GEN/BenhAn.jasper");
          }
            
          jasperIs = new FileInputStream(new File(jrxmlFile));
          
          jasperPrint = JasperFillManager.fillReport(jasperIs,
              params, new JRBeanCollectionDataSource(dsHsba));
            
            //if (!"1".equals(fromCsdlTg)) {
                //getEmrLogTruyCapHsbaService().luuEmrLogTruyCapHsba(emrLogTruyCapHsba);  
            //} 
        }
        
        var ouputStream = new ByteArrayOutputStream();

        var exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(ouputStream));

        exporter.exportReport();
        ouputStream.close();
        return ouputStream.toByteArray();
    }
}
