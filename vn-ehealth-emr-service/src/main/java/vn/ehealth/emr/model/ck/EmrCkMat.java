package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkMat {

    public String thiluckhongkinhtrai;
    
    public String thiluckhongkinhphai;
    
    public String thiluccokinhtrai;
    
    public String thiluccokinhphai;
    
    public String nhanaptrai;
    
    public String nhanapphai;
    
    public String thitruongtrai;
    
    public String thitruongphai;
    
    public Boolean choimattrai;
    
    public Boolean choimatphai;
    
    public Boolean chaynuocmattrai;
    
    public Boolean chaynuocmatphai;
    
    public Boolean sosangmattrai;
    
    public Boolean sosangmatphai;
    
    public Boolean momattrai;
    
    public Boolean momatphai;
    
    public Boolean rucmattrai;
    
    public Boolean rucmatphai;
    
    
    
    public String trieuchungmattrai;
    
    public String trieuchungmatphai;
    //le dao
    public String ledaotrai;   
    public String ledaophai;
    //mi mat
    public String mimattrai;   
    public String mimatphai;
    
    //ket mac
    public String ketmacmattrai;   
    public String ketmacmatphai;
    
    //tinh hinh mat hoi
    public String mathoitrai;  
    public String mathoiphai;
    //giac mac
    public String giaccungmacmattrai;  
    public String giaccungmacmatphai;
    //cung  mac
    public String cungmacmattrai;  
    public String cungmacmatphai;
    
    //tien phong
    public String tienphongmattrai;    
    public String tienphongmatphai;
    
    //mong mat
    public String mongmattrai; 
    public String mongmatphai;
    
    //dong tu phan xa
    public String dongtumattrai;   
    public String dongtumatphai;
    
    //dich kinh(thuy tinh dich)
    public String dichkinhmattrai; 
    public String dichkinhmatphai;
    
    //thuy tinh the
    public String thethuytinhmattrai;  
    public String thethuytinhmatphai;
    
    //soi sang dong tu
    public String soisangdongtumattrai;    
    public String soisangdongtumatphai;
    //goc tien phong 
    public String goctienphongmattrai; 
    public String goctienphongmatphai; 
    
    //nhan cau 
    public String nhancaumattrai;  
    public String nhancaumatphai;
    
    //van nhan
    public String vannhanmattrai;  
    public String vannhanmatphai;
    
    //ho mat
    public String homattrai;   
    public String homatphai;   
    //day mat
    public String daymattrai;  
    public String daymatphai;  
}
