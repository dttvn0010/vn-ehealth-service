package vn.ehealth.cdr.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.cdr.model.component.EmrRef;
import vn.ehealth.cdr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "can_bo_y_te")
public class CanboYte {

    @Id public ObjectId id;
    public EmrRef coSoKhamBenhRef;
    public EmrRef emrPersonRef;
    
    public String ten;
    public String chungChiHanhNghe;
    
    public String getId() {
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) { 
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public static EmrRef toEmrRef(CanboYte obj) {
        if(obj == null || obj.id == null) return null;
        
        var ref = new EmrRef();
        ref.className = CanboYte.class.toString();
        ref.objectId = obj.id;
        ref.name = obj.ten;
        ref.identifier = obj.chungChiHanhNghe;
        
        return ref;
    }
}
