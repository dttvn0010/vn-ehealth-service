package vn.ehealth.cdr.model;

import org.bson.types.ObjectId;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CanboYte {

    public String ten;
    public String chungChiHanhNghe;
    
    @JsonIgnore
    public ObjectId emrPersonId;
   
    public CanboYte() {
        
    }
    
    public CanboYte(String ten) {
        this.ten = ten;
    }
    
    public static Reference toRef(CanboYte dto) {
        if(dto != null) {
            var ref = new Reference();
            ref.setDisplay(dto.ten);
            return ref;
        }
        return null;
    }
}
