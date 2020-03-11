package vn.ehealth.emr.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrTongKetSanKhoa {

    public EmrDmContent emrDmTrangThaiCoTucung;
  
    public EmrDmContent emrDmTrangThaiAmdao;
   
    public EmrDmContent emrDmTrangThaiSinhmon;
    
    public EmrDmContent emrDmCachDe;
    
    public Date deluc;
    
    public Integer apgar01;
    
    public Integer apgar05;
    
    public Integer apgar10;
    
    public String sscannang;
    
    public Integer sschieucao;
    
    public Integer ssvongdau;
    
    public Boolean ssdathai;
    
    public Integer sssotrai;
    
    public Integer sssogai;
    
    public Boolean ssditathaumon;
    
    public Boolean ssditatkhac;
    
    public String ssmotaditat;
    
    public String sstinhtrang;
    
    public Boolean sshutdich;
    
    public Boolean ssxoaboptim;
    
    public Boolean ssthoo2;
    
    public Boolean ssnoikhiquan;
    
    public Boolean ssbopbongo2;
    
    public Boolean sshoisinhkhac;
    
    public Date rausoluc;
    
    public Boolean somatmang;
    
    public Boolean somatmui;
    
    public Boolean raubongnon;
    
    public Integer thoigiansorau;
    
    public Integer chieudairau;
    
    public Boolean raucuonso;
    
    public Boolean kiemsoattucung;
    
    public String lydokiemsoat;
    
    public Boolean bocraunhantao;
    
    public String lydobocrau;
    
    public Boolean chaymausauso;
    
    public Integer luongmaumat;
    
    public String toantrang;
    
    public Integer mach;
    
    public Integer nhietdo;
    
    public Integer huyetapthap;
    
    public Integer huyetapcao;
    
    public Integer nhiptho;
    
    public String lydocanthiep;
    
    public String canthiepkhac;
    
    // Add moi 13/04/2015
    public String tennguoitheodoi;
    
    public String chucdanhnguoitheodoi;
    
    public String motachamsoctresosinh;
    
    public Integer loaisorau;
    
    public String motasorau;
    
    public String motamatmang;
    
    public String motamatmui;
    
    public String motabanhrau;
    
    public String cannangrau;
    
    public String luongmaubimat;
    
    public String xulysorau;
    
    public String motatoantrang;
    
    public String lydocanthiepkhac;
    
    public String phuongphapkhauloaichi;
    
    public Integer somuikhau;
    
    public String tennguoimo;
    
    public String tennguoidode;
    
    public String thoigianchuyenda;
    
    public String thoigiantheodoikhoade;

    
    public Integer apgar1pTim;
    
    public Integer apgar1pTho;
        
    public Integer apgar1pMausacda;
    
    public Integer apgar1pTruonglucco;
    
    public Integer apgar1pPhanxa;
    
    public Integer apgar5pTim;
    
    public Integer apgar5pTho;
    
    public Integer apgar5pMausacda;
    
    public Integer apgar5pTruonglucco;
    
    public Integer apgar5pPhanxa;
    
    public Integer soluongsong;
    
    public Integer soluongchet;

}
