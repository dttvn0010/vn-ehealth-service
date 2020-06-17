package vn.ehealth.cdr.model.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import vn.ehealth.auth.model.User;

public class EmrRef {
    public String className;
    public String objectId;
    public String name;
    public String code;
    public String identifier;
    public Map<String, String> properties = new HashMap<String, String>();
    
    
    public static ObjectId toObjectId(EmrRef ref) {
        if(ref == null || StringUtils.isEmpty(ref.objectId)) {
            return null;
        }
        
        return new ObjectId(ref.objectId);
        
    }
    
    public static EmrRef fromUser(User user) {
        if(user == null || user.id == null) {
            return null;
        }
        
        var ref = new EmrRef();
        ref.className = User.class.getName();
        ref.objectId = user.getId();
        ref.name = user.getDisplay();
        ref.identifier = user.username;
        
        return ref;
    }
}
