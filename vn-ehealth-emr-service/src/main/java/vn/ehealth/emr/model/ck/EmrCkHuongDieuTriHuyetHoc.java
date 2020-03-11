package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkHuongDieuTriHuyetHoc {

    public Integer hongcauSolan;
    public Double hongcauTongluong;
    public Integer hongcauruaSolan;
    public Double hongcauruaTongluong;
    public Integer tieucauSolan;
    public Double tieucauTongluong;
    public Integer bachcauSolan;
    public Double bachcauTongluong;
    public Integer huyettuongSolan;
    public Double huyettuongTongluong;
    public Integer huyettuongtuoiSolan;
    public Double huyettuongtuoiTongluong;
    public Integer tualanhSolan;
    public Double tualanhTongluong;
    public Integer mautoanphanSolan;
    public Double mautoanphanTongluong;
    public Integer phanungtruyenmau;
}
