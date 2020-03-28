package vn.ehealth.emr.model;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import vn.ehealth.emr.utils.ObjectIdUtil;

@JsonInclude(Include.NON_NULL)
@Document(collection = "emr_dm")
public class EmrDm {

    @Id public ObjectId id;    
    
    public ObjectId emrNhomDmId;
    public ObjectId emrDmChaId;
    public int trangThai;
    public String ma = "";
    public String ten = "";
    public int capdo;
    
    public Map<String, Object> extension;
    
    public String getId() { 
        return ObjectIdUtil.idToString(id);
    }
    
    public void setId(String id) {
        this.id = ObjectIdUtil.stringToId(id);
    }
    
    public String getEmrNhomDmId() {
        return ObjectIdUtil.idToString(emrNhomDmId);
    }
    
    public String getEmrDmChaId() {
        return ObjectIdUtil.idToString(emrDmChaId);
    }
}
