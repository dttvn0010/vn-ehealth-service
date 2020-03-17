package vn.ehealth.emr.model;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrKhoaDieuTri {

    public EmrDmContent emrDmKhoaDieuTri;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngaygiovaokhoa;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    public Date ngayketthucdieutri;

    public String tenkhoa;

    public EmrCanboYte bacsidieutri;

    public String phong;

    public String giuong;

    public Integer songaydieutri;

    public String tentruongkhoa;    
}
