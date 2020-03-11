package vn.ehealth.emr.model.ck;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class EmrCkChanTayMieng {

    public Boolean timtai;
    public Integer spo2;
    public Integer trigiac;
    public Boolean loetmieng;
    public Boolean phatban;
}
