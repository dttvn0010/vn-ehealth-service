package vn.ehealth.cdr.model.component;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.auth.model.User;

@JsonInclude(Include.NON_NULL)
public class CanboYteDTO {

    public String ten;
    public String chungChiHanhNghe;
       
    public CanboYteDTO() {
        
    }
    
    public CanboYteDTO(User user) {
        this.ten = StringUtils.isEmpty(user.tenDayDu)? user.username : user.tenDayDu;
        this.chungChiHanhNghe = user.chungChiHanhNghe;
    }
    
    public CanboYteDTO(String ten) {
        this.ten = ten;
    }
        
    public static Reference toRef(CanboYteDTO dto) {
        if(dto != null) {
            var ref = new Reference();
            ref.setDisplay(dto.ten);
            return ref;
        }
        return null;
    }
}
