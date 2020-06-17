package vn.ehealth.cdr.model.component;

import org.hl7.fhir.r4.model.Reference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.CanboYte;
import vn.ehealth.cdr.service.ServiceFactory;

@JsonInclude(Include.NON_NULL)
public class CanboYteDTO {

    public String ten;
    public String chungChiHanhNghe;
       
    public CanboYteDTO() {
        
    }
    
    public CanboYteDTO(String ten) {
        this.ten = ten;
    }
    
    public static EmrRef toEmrRef(CanboYteDTO dto) {
        if(dto == null) return null;
                
        var ref = new EmrRef();
        ref.identifier = dto.chungChiHanhNghe;
        ref.name = dto.ten;
        ref.className = CanboYte.class.getName();
        
        var obj = ServiceFactory.getCanboYteService().getByChungChiHanhNghe(dto.chungChiHanhNghe).orElse(null);
        if(obj != null) {
            ref.objectId = obj.id.toHexString();
        }
        
        return ref;
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