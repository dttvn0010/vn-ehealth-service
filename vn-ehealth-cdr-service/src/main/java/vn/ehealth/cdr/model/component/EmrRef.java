package vn.ehealth.cdr.model.component;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import vn.ehealth.auth.model.User;

public class EmrRef {
    public String className;
    public ObjectId objectId;
    public String name;
    public String code;
    public String identifier;
    public Map<String, String> properties = new HashMap<String, String>();
    
    public String getObjectId() {
        return objectId != null? objectId.toHexString(): null;
    }
    
    public void setObjectId(String objectId) {
        if(objectId != null) {
            this.objectId = new ObjectId(objectId);
        }else {
            this.objectId = null;
        }
    }
    
    public static EmrRef fromObjectId(String className, String objectId) {
        var ref = new EmrRef();
        ref.className = className;
        ref.objectId = new ObjectId(objectId);
        return ref;        
    }
    
    public static ObjectId toObjectId(EmrRef ref) {
        if(ref == null) {
            return null;
        }
        
        return ref.objectId;        
    }
    
    public static EmrRef fromUser(User user) {
        if(user == null || user.id == null) {
            return null;
        }
        
        var ref = new EmrRef();
        ref.className = User.class.getName();
        ref.objectId = user.id;
        ref.name = user.getDisplay();
        ref.identifier = user.username;
        
        return ref;
    }
}
