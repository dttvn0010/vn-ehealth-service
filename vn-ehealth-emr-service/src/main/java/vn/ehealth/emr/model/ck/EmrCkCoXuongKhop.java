package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkCoXuongKhop {

    public String hinhthekhop;
    
    public String tamhoatdongvaovien;

    public String tamhoatdonravien;

    public String ttbenhlyco;

    public String roiloanco;

    public String tencothu;

    public Integer baccothu;

    public String cotsongttbly;

    public String cotsongschober;

    public String cotsongstibor;

    public String cotsongroiloancn;
}
