package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkTieuHoa {

    public Boolean ganto;
    public Double gankichthuoc;
    public String gandacdiem;
    public String motakhac;
    public String tinhtrangbenhly;
    public String roiloanchucnang;
}
