package vn.ehealth.emr.utils;

import org.bson.types.ObjectId;

public class ObjectIdUtil {

    public static String idToString(ObjectId id) {
        return id != null? id.toHexString() : null;
    }
    
    public static ObjectId stringToId(String id) {
        return id != null? new ObjectId(id) : null;
    }
}
