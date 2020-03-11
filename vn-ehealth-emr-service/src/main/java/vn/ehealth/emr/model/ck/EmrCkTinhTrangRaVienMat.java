package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTinhTrangRaVienMat {

    public String thiluccokinhphai;
    public String thiluccokinhtrai;
    public String thiluckhongkinhphai;
    public String thiluckhongkinhtrai;
    public String nhanapphai;
    public String nhanaptrai;
}
