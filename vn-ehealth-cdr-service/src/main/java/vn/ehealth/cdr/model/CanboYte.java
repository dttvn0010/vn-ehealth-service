package vn.ehealth.cdr.model;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CanboYte {

    public String ten;
    public String chungChiHanhNghe;
    
    @JsonIgnore
    public ObjectId emrPersonId;
   
    public static Reference toRef(CanboYte dto) {
        if(dto != null) {
            var ref = new Reference();
            ref.setDisplay(dto.ten);
            return ref;
        }
        return null;
    }
}
